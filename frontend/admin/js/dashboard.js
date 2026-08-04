// dashboard.js
import { api } from './api.js';

document.addEventListener('DOMContentLoaded', async () => {
    await updateDashboardStats();
});

export async function updateDashboardStats() {
    const reservations = await api.getReservations();
    const copies = await api.getCopies();

    const pending = reservations.filter(r => r.status === 'SUBMITTED' || r.status === 'UNDER_REVIEW').length;
    const issued = reservations.filter(r => r.status === 'ISSUED').length;
    
    // Business Rule: Unavailable copies include Damaged, Repair, Reference Only, Lost
    const unavailableStatuses = ['DAMAGED', 'REPAIR', 'REFERENCE_ONLY', 'LOST'];
    const unavailable = copies.filter(c => unavailableStatuses.includes(c.status)).length;
    
    const today = new Date('2026-08-04T00:00:00'); // Simulated today
    const overdue = reservations.filter(r => {
        if (r.status !== 'ISSUED') return false;
        return new Date(r.returnDate) < today;
    }).length;

    const pendingEl = document.getElementById('stat-pending');
    const issuedEl = document.getElementById('stat-issued');
    const unavailableEl = document.getElementById('stat-unavailable');
    const overdueEl = document.getElementById('stat-overdue');

    if (pendingEl) pendingEl.textContent = pending;
    if (issuedEl) issuedEl.textContent = issued;
    if (unavailableEl) unavailableEl.textContent = unavailable;
    if (overdueEl) overdueEl.textContent = overdue;
}
