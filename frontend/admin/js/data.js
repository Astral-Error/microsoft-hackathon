// data.js - Common seed database
export const initialData = {
    users: [
        { id: 'm1', username: 'student', password: 'student123', name: 'Alice', role: 'MEMBER' },
        { id: 'l1', username: 'librarian', password: 'admin123', name: 'Admin', role: 'LIBRARIAN' }
    ],
    books: [
        { id: 'b1', title: 'Clean Code', category: 'Software Engineering', totalCopies: 4 }
    ],
    copies: [
        { id: 'b1-c1', bookId: 'b1', status: 'AVAILABLE' },
        { id: 'b1-c2', bookId: 'b1', status: 'AVAILABLE' },
        { id: 'b1-c3', bookId: 'b1', status: 'AVAILABLE' },
        { id: 'b1-c4', bookId: 'b1', status: 'AVAILABLE' }
    ],
    reservations: [
        // Seed some pending requests for the queue
        { id: 'r1', memberId: 'm1', bookId: 'b1', copyId: null, startDate: '2026-08-05', returnDate: '2026-08-10', status: 'SUBMITTED', rejectionReason: null }
    ]
};
