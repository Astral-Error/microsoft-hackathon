// auth.js
import { api } from './api.js';

document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            e.stopPropagation();

            // Business Rule: Validate form fields
            if (!loginForm.checkValidity()) {
                loginForm.classList.add('was-validated');
                return;
            }

            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const errorMsg = document.getElementById('login-error');

            const res = await api.login(username, password);
            if (res.success) {
                // Business Rule: Redirect based on role
                if (res.user.role === 'LIBRARIAN') {
                    window.location.href = 'adminDashboard.html';
                } else {
                    // Redirect to member dashboard (built by Person 1)
                    window.location.href = 'dashboard.html';
                }
            } else {
                errorMsg.classList.remove('d-none');
                errorMsg.textContent = res.message;
                loginForm.classList.remove('was-validated');
            }
        });
    }

    // Protect pages logic
    const currentUser = api.getCurrentUser();
    const currentPage = window.location.pathname.split('/').pop();

    if (currentPage && currentPage !== 'login.html' && currentPage !== 'index.html' && currentPage !== '') {
        if (!currentUser) {
            window.location.href = 'login.html';
        } else {
            // Check Admin routes
            const adminRoutes = ['adminDashboard.html', 'reviewQueue.html', 'copyManagement.html', 'calendar.html'];
            if (adminRoutes.includes(currentPage) && currentUser.role !== 'LIBRARIAN') {
                window.location.href = 'dashboard.html'; // Redirect member away from admin
            }

            // Setup navbar UI
            const userLabel = document.getElementById('nav-user-name');
            const roleBadge = document.getElementById('nav-role-badge');
            if (userLabel) userLabel.textContent = currentUser.name;
            if (roleBadge) {
                roleBadge.textContent = currentUser.role;
                roleBadge.className = currentUser.role === 'LIBRARIAN' ? 'badge bg-primary' : 'badge bg-info';
            }

            // Logout listener
            const logoutBtn = document.getElementById('logout-btn');
            if (logoutBtn) {
                logoutBtn.addEventListener('click', async (e) => {
                    e.preventDefault();
                    await api.logout();
                    window.location.href = 'login.html';
                });
            }
        }
    }
});
