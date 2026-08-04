// api.js - Simulates REST API using LocalStorage
import { initialData } from './data.js';

// Initialize DB
if (!localStorage.getItem('lib_db_v2')) {
    localStorage.setItem('lib_db_v2', JSON.stringify(initialData));
}

const db = JSON.parse(localStorage.getItem('lib_db_v2'));

function saveDb() {
    localStorage.setItem('lib_db_v2', JSON.stringify(db));
}

export const api = {
    login: async (username, password) => {
        // Business Rule 1: Validate credentials against stored users
        const user = db.users.find(u => u.username === username && u.password === password);
        if (user) {
            const sessionUser = { id: user.id, username: user.username, role: user.role, name: user.name };
            localStorage.setItem('user', JSON.stringify(sessionUser));
            return { success: true, user: sessionUser };
        }
        return { success: false, message: 'Invalid username or password' };
    },
    
    logout: async () => {
        localStorage.removeItem('user');
    },

    getCurrentUser: () => {
        return JSON.parse(localStorage.getItem('user'));
    },

    getReservations: async () => {
        return [...db.reservations];
    },

    updateReservationStatus: async (id, newStatus, reason = null, copyId = null) => {
        const req = db.reservations.find(r => r.id === id);
        if (req) {
            req.status = newStatus;
            if (reason) req.rejectionReason = reason;
            if (copyId) req.copyId = copyId;
            saveDb();
            return { success: true };
        }
        return { success: false };
    },

    getCopies: async () => {
        return [...db.copies];
    },

    updateCopyStatus: async (copyId, newStatus) => {
        const copy = db.copies.find(c => c.id === copyId);
        if (copy) {
            copy.status = newStatus;
            saveDb();
            return { success: true };
        }
        return { success: false };
    },
    
    getBooks: async () => {
        return [...db.books];
    },
    
    getUsers: async () => {
        return [...db.users];
    }
};
