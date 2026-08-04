// calendar.js
import { api } from './api.js';

let currentMonth = 7; // Aug (0-indexed)
let currentYear = 2026;
let eventModalInstance;
let cachedData = {};

document.addEventListener('DOMContentLoaded', async () => {
    eventModalInstance = new bootstrap.Modal(document.getElementById('eventModal'));
    
    document.getElementById('prev-month').addEventListener('click', () => {
        currentMonth--;
        if (currentMonth < 0) { currentMonth = 11; currentYear--; }
        renderCalendar();
    });
    
    document.getElementById('next-month').addEventListener('click', () => {
        currentMonth++;
        if (currentMonth > 11) { currentMonth = 0; currentYear++; }
        renderCalendar();
    });

    await fetchData();
    renderCalendar();
});

async function fetchData() {
    cachedData.reqs = await api.getReservations();
    cachedData.books = await api.getBooks();
    cachedData.users = await api.getUsers();
}

function getBadgeClass(status) {
    switch(status) {
        case 'AVAILABLE': case 'RETURNED': return 'bg-success';
        case 'UNDER_REVIEW': case 'RESERVED': case 'SUBMITTED': return 'bg-warning text-dark';
        case 'CONFIRMED': return 'bg-success';
        case 'ISSUED': return 'bg-info text-dark';
        case 'REJECTED': case 'DAMAGED': case 'LOST': return 'bg-danger';
        case 'CANCELLED': return 'bg-secondary';
        case 'REPAIR': return 'bg-primary'; 
        default: return 'bg-secondary';
    }
}

function renderCalendar() {
    const container = document.getElementById('calendar-container');
    const monthLabel = document.getElementById('current-month-label');
    
    const monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
    monthLabel.textContent = `${monthNames[currentMonth]} ${currentYear}`;

    const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();
    const firstDay = new Date(currentYear, currentMonth, 1).getDay();

    let html = `
        <div class="calendar-grid">
            <div class="calendar-day-header">Sun</div>
            <div class="calendar-day-header">Mon</div>
            <div class="calendar-day-header">Tue</div>
            <div class="calendar-day-header">Wed</div>
            <div class="calendar-day-header">Thu</div>
            <div class="calendar-day-header">Fri</div>
            <div class="calendar-day-header">Sat</div>
    `;

    for (let i = 0; i < firstDay; i++) {
        html += `<div class="calendar-day empty"></div>`;
    }

    const today = new Date('2026-08-04T00:00:00');

    // Filter relevant requests
    const activeReqs = cachedData.reqs.filter(r => ['CONFIRMED', 'ISSUED'].includes(r.status));

    for (let i = 1; i <= daysInMonth; i++) {
        const currentDateStr = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`;
        
        // Find events
        const issueEvents = activeReqs.filter(r => r.startDate === currentDateStr);
        const returnEvents = activeReqs.filter(r => r.returnDate === currentDateStr);
        
        let eventsHtml = '';
        
        issueEvents.forEach(req => {
            const book = cachedData.books.find(b => b.id === req.bookId);
            const user = cachedData.users.find(u => u.id === req.memberId);
            eventsHtml += `<div class="cal-event-issue rounded-pill small px-2 mt-1 shadow-sm event-clk w-100" style="font-size: 0.7rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" data-id="${req.id}" title="${book.title} | ${user.name} | ${req.status}">${book.title} (Issue)</div>`;
        });
        
        returnEvents.forEach(req => {
            const book = cachedData.books.find(b => b.id === req.bookId);
            const user = cachedData.users.find(u => u.id === req.memberId);
            eventsHtml += `<div class="cal-event-return rounded-pill small px-2 mt-1 shadow-sm event-clk w-100" style="font-size: 0.7rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;" data-id="${req.id}" title="${book.title} | ${user.name} | ${req.status}">${book.title} (Due)</div>`;
        });

        const isToday = (currentYear === today.getFullYear() && currentMonth === today.getMonth() && i === today.getDate()) ? 'border border-primary border-2' : '';

        let dayNumClass = isToday ? 'text-primary' : 'text-muted';
        if (returnEvents.length > 0) {
            dayNumClass = 'text-danger';
        }

        html += `
            <div class="calendar-day align-items-start p-1 flex-column ${isToday}" style="aspect-ratio: auto; min-height: 80px; border-radius: 8px; border: 1px solid #dee2e6;">
                <div class="w-100 text-end mb-1"><span class="fw-bold ${dayNumClass}">${i}</span></div>
                ${eventsHtml}
            </div>
        `;
    }

    html += `</div>`;
    container.innerHTML = html;

    // Attach click listeners to events
    document.querySelectorAll('.event-clk').forEach(el => {
        el.addEventListener('click', (e) => {
            e.stopPropagation();
            const reqId = e.target.getAttribute('data-id');
            showEventModal(reqId);
        });
    });
}

function showEventModal(reqId) {
    const req = cachedData.reqs.find(r => r.id === reqId);
    const book = cachedData.books.find(b => b.id === req.bookId);
    const user = cachedData.users.find(u => u.id === req.memberId);
    
    document.getElementById('event-book').textContent = book.title;
    document.getElementById('event-copy').textContent = req.copyId || 'Unassigned';
    document.getElementById('event-member').textContent = user.name;
    document.getElementById('event-start').textContent = req.startDate;
    document.getElementById('event-end').textContent = req.returnDate;
    
    const statusEl = document.getElementById('event-status');
    statusEl.className = `badge rounded-pill ${getBadgeClass(req.status)}`;
    statusEl.textContent = req.status;

    eventModalInstance.show();
}
