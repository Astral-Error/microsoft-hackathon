// copyManagement.js
import { api } from './api.js';
import { notifications } from './notifications.js';

let changedCopies = {};

document.addEventListener('DOMContentLoaded', async () => {
    await renderCopies();
    
    document.getElementById('save-changes-btn').addEventListener('click', saveChanges);
});

function getBadgeClass(status) {
    switch(status) {
        case 'AVAILABLE': return 'bg-success';
        case 'RESERVED': return 'bg-warning text-dark';
        case 'ISSUED': return 'bg-info text-dark';
        case 'DAMAGED': return 'bg-danger';
        case 'REPAIR': return 'bg-primary'; 
        case 'REFERENCE_ONLY': return 'bg-secondary';
        case 'LOST': return 'bg-dark';
        default: return 'bg-secondary';
    }
}

async function renderCopies() {
    const container = document.getElementById('copies-container');
    if (!container) return;
    container.innerHTML = '';
    changedCopies = {}; // reset

    const books = await api.getBooks();
    const copies = await api.getCopies();
    const reqs = await api.getReservations();
    const users = await api.getUsers();

    books.forEach(book => {
        const bookCopies = copies.filter(c => c.bookId === book.id);
        if (bookCopies.length === 0) return;

        const bookSection = document.createElement('div');
        bookSection.className = 'mb-5';
        bookSection.innerHTML = `<h6 class="fw-bold text-uppercase text-muted border-bottom pb-2 mb-3">${book.title}</h6>`;
        
        const table = document.createElement('table');
        table.className = 'table table-hover align-middle mb-0';
        table.innerHTML = `
            <thead class="table-light">
                <tr>
                    <th style="width: 20%;">Copy ID</th>
                    <th style="width: 15%;">Current Status</th>
                    <th style="width: 25%;">Active Reservation / Member</th>
                    <th style="width: 20%;">Dates</th>
                    <th style="width: 20%;" class="text-end">Update Status</th>
                </tr>
            </thead>
            <tbody></tbody>
        `;
        const tbody = table.querySelector('tbody');

        bookCopies.forEach(copy => {
            // Find if there's an active reservation for this copy
            const activeReq = reqs.find(r => r.copyId === copy.id && ['CONFIRMED', 'ISSUED'].includes(r.status));
            
            let memberInfo = '<span class="text-muted small">None</span>';
            let datesInfo = '<span class="text-muted small">-</span>';
            
            if (activeReq) {
                const user = users.find(u => u.id === activeReq.memberId);
                memberInfo = `<div class="small fw-semibold">${user.name}</div>`;
                datesInfo = `<div class="small">${activeReq.startDate} to ${activeReq.returnDate}</div>`;
            }

            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td class="fw-bold text-secondary">${copy.id}</td>
                <td><span class="badge rounded-pill ${getBadgeClass(copy.status)}" id="badge-${copy.id}">${copy.status}</span></td>
                <td>${memberInfo}</td>
                <td>${datesInfo}</td>
                <td class="text-end">
                    <select class="form-select form-select-sm d-inline-block w-auto status-dropdown" data-id="${copy.id}">
                        <option value="AVAILABLE" ${copy.status === 'AVAILABLE' ? 'selected' : ''}>Available</option>
                        <option value="RESERVED" ${copy.status === 'RESERVED' ? 'selected' : ''} disabled>Reserved</option>
                        <option value="ISSUED" ${copy.status === 'ISSUED' ? 'selected' : ''} disabled>Issued</option>
                        <option value="DAMAGED" ${copy.status === 'DAMAGED' ? 'selected' : ''}>Damaged</option>
                        <option value="REPAIR" ${copy.status === 'REPAIR' ? 'selected' : ''}>Under Repair</option>
                        <option value="REFERENCE_ONLY" ${copy.status === 'REFERENCE_ONLY' ? 'selected' : ''}>Reference Only</option>
                        <option value="LOST" ${copy.status === 'LOST' ? 'selected' : ''}>Lost</option>
                    </select>
                </td>
            `;
            tbody.appendChild(tr);
        });

        bookSection.appendChild(table);
        container.appendChild(bookSection);
    });

    document.querySelectorAll('.status-dropdown').forEach(select => {
        select.addEventListener('change', e => {
            const copyId = e.target.getAttribute('data-id');
            changedCopies[copyId] = e.target.value;
            
            // visually update the badge immediately for UX
            const badge = document.getElementById(`badge-${copyId}`);
            if (badge) {
                badge.className = `badge rounded-pill ${getBadgeClass(e.target.value)}`;
                badge.textContent = e.target.value;
            }
        });
    });
}

async function saveChanges() {
    const ids = Object.keys(changedCopies);
    if (ids.length === 0) {
        notifications.showToast('No changes to save.', 'info');
        return;
    }

    for (let id of ids) {
        await api.updateCopyStatus(id, changedCopies[id]);
    }

    notifications.showToast('Physical copies updated successfully.', 'success');
    await renderCopies(); // refresh state
}
