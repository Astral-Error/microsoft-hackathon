// reviewQueue.js
import { api } from './api.js';
import { notifications } from './notifications.js';

let rejectModalInstance;
let returnModalInstance;

document.addEventListener('DOMContentLoaded', async () => {
    rejectModalInstance = new bootstrap.Modal(document.getElementById('rejectModal'));
    returnModalInstance = new bootstrap.Modal(document.getElementById('returnModal'));
    
    await renderQueue();

    // Event Listeners for Modals
    document.getElementById('confirm-reject-btn').addEventListener('click', handleRejectConfirm);
    document.getElementById('confirm-return-btn').addEventListener('click', handleReturnConfirm);
});

function getBadgeClass(status) {
    switch(status) {
        case 'AVAILABLE': case 'RETURNED': return 'bg-success';
        case 'UNDER_REVIEW': case 'RESERVED': case 'SUBMITTED': return 'bg-warning text-dark';
        case 'CONFIRMED': return 'bg-success';
        case 'ISSUED': return 'bg-info text-dark';
        case 'REJECTED': case 'DAMAGED': case 'LOST': return 'bg-danger';
        case 'CANCELLED': return 'bg-secondary';
        case 'REPAIR': return 'bg-primary'; // purple equivalent in our styles
        default: return 'bg-secondary';
    }
}

async function renderQueue() {
    const tbody = document.getElementById('queue-table-body');
    if (!tbody) return;
    tbody.innerHTML = '';

    const reqs = await api.getReservations();
    const books = await api.getBooks();
    const users = await api.getUsers();

    // Filter relevant statuses
    const queue = reqs.filter(r => ['SUBMITTED', 'UNDER_REVIEW', 'CONFIRMED', 'ISSUED'].includes(r.status));

    if (queue.length === 0) {
        tbody.innerHTML = `<tr><td colspan="4" class="text-center text-muted py-4">No pending requests or active loans.</td></tr>`;
        return;
    }

    queue.forEach(req => {
        const book = books.find(b => b.id === req.bookId);
        const user = users.find(u => u.id === req.memberId);
        
        let actions = '';
        if (req.status === 'SUBMITTED' || req.status === 'UNDER_REVIEW') {
            actions = `
                <button class="btn btn-sm btn-success fw-bold me-2 approve-btn" data-id="${req.id}">Approve</button>
                <button class="btn btn-sm btn-outline-danger reject-btn" data-id="${req.id}">Reject</button>
            `;
        } else if (req.status === 'CONFIRMED') {
            // Business Rule: Issue button only on confirmed reservations
            actions = `<button class="btn btn-sm btn-info text-white fw-bold issue-btn" data-id="${req.id}">Issue Book</button>`;
        } else if (req.status === 'ISSUED') {
            actions = `<button class="btn btn-sm btn-primary fw-bold return-btn" data-id="${req.id}" data-copy="${req.copyId}">Process Return</button>`;
        }

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>
                <div class="fw-bold">${book.title}</div>
                <div class="text-muted small">Req by: ${user.name}</div>
            </td>
            <td>
                <div class="small">${req.startDate} to ${req.returnDate}</div>
            </td>
            <td>
                <span class="badge rounded-pill ${getBadgeClass(req.status)}">${req.status}</span>
            </td>
            <td class="text-end">
                ${actions}
            </td>
        `;
        tbody.appendChild(tr);
    });

    // Attach listeners
    document.querySelectorAll('.approve-btn').forEach(b => b.addEventListener('click', handleApprove));
    document.querySelectorAll('.reject-btn').forEach(b => b.addEventListener('click', openRejectModal));
    document.querySelectorAll('.issue-btn').forEach(b => b.addEventListener('click', handleIssue));
    document.querySelectorAll('.return-btn').forEach(b => b.addEventListener('click', openReturnModal));
}

// Logic implementations
async function handleApprove(e) {
    const id = e.target.getAttribute('data-id');
    const reqs = await api.getReservations();
    const req = reqs.find(r => r.id === id);
    
    // Business Rule: Auto-assign an available copy
    const copies = await api.getCopies();
    const availableCopy = copies.find(c => c.bookId === req.bookId && c.status === 'AVAILABLE');
    
    if (availableCopy) {
        await api.updateReservationStatus(id, 'CONFIRMED', null, availableCopy.id);
        notifications.showToast('Request Approved and Copy Assigned.', 'success');
        renderQueue();
    } else {
        notifications.showToast('No functional copies available for this title.', 'error');
    }
}

function openRejectModal(e) {
    const id = e.target.getAttribute('data-id');
    document.getElementById('reject-req-id').value = id;
    document.getElementById('reject-reason').value = '';
    rejectModalInstance.show();
}

async function handleRejectConfirm() {
    const id = document.getElementById('reject-req-id').value;
    const reason = document.getElementById('reject-reason').value;
    
    // Business Rule: Reject requires reason
    if (!reason.trim()) {
        notifications.showToast('Reason is mandatory for rejection.', 'error');
        return;
    }
    
    await api.updateReservationStatus(id, 'REJECTED', reason);
    rejectModalInstance.hide();
    notifications.showToast('Request Rejected.', 'success');
    renderQueue();
}

async function handleIssue(e) {
    const id = e.target.getAttribute('data-id');
    
    // Business Rule: Status becomes Issued
    await api.updateReservationStatus(id, 'ISSUED');
    notifications.showToast('Book has been issued.', 'success');
    renderQueue();
}

function openReturnModal(e) {
    const id = e.target.getAttribute('data-id');
    const copyId = e.target.getAttribute('data-copy');
    
    document.getElementById('return-req-id').value = id;
    document.getElementById('return-copy-id').value = copyId;
    document.getElementById('return-condition').value = 'AVAILABLE';
    
    returnModalInstance.show();
}

async function handleReturnConfirm() {
    const id = document.getElementById('return-req-id').value;
    const copyId = document.getElementById('return-copy-id').value;
    const condition = document.getElementById('return-condition').value;
    
    await api.updateReservationStatus(id, 'RETURNED');
    
    // Business Rule: Update copy availability on return
    await api.updateCopyStatus(copyId, condition);
    
    returnModalInstance.hide();
    notifications.showToast(`Book returned. Copy marked as ${condition}.`, 'success');
    renderQueue();
}
