// data.js - Common seed database
export const initialData = {
    users: [
        { id: 'm1', username: 'student', password: 'student123', name: 'Alice', role: 'MEMBER' },
        { id: 'm2', username: 'bob', password: 'password', name: 'Bob', role: 'MEMBER' },
        { id: 'm3', username: 'charlie', password: 'password', name: 'Charlie', role: 'MEMBER' },
        { id: 'l1', username: 'librarian', password: 'admin123', name: 'Admin', role: 'LIBRARIAN' }
    ],
    books: [
        { id: 'b1', title: 'Clean Code', category: 'Software Engineering', totalCopies: 4 },
        { id: 'b2', title: 'Dune', category: 'Science Fiction', totalCopies: 2 },
        { id: 'b3', title: '1984', category: 'Dystopian', totalCopies: 3 },
        { id: 'b4', title: 'The Pragmatic Programmer', category: 'Software Engineering', totalCopies: 1 }
    ],
    copies: [
        // Clean Code copies
        { id: 'b1-c1', bookId: 'b1', status: 'AVAILABLE' },
        { id: 'b1-c2', bookId: 'b1', status: 'ISSUED' },
        { id: 'b1-c3', bookId: 'b1', status: 'REPAIR' },
        { id: 'b1-c4', bookId: 'b1', status: 'LOST' },
        // Dune copies
        { id: 'b2-c1', bookId: 'b2', status: 'AVAILABLE' },
        { id: 'b2-c2', bookId: 'b2', status: 'ISSUED' },
        // 1984 copies
        { id: 'b3-c1', bookId: 'b3', status: 'AVAILABLE' },
        { id: 'b3-c2', bookId: 'b3', status: 'RESERVED' },
        { id: 'b3-c3', bookId: 'b3', status: 'DAMAGED' },
        // Pragmatic Programmer
        { id: 'b4-c1', bookId: 'b4', status: 'AVAILABLE' }
    ],
    reservations: [
        // Issued (Visible on Calendar)
        { id: 'r1', memberId: 'm1', bookId: 'b1', copyId: 'b1-c2', startDate: '2026-08-01', returnDate: '2026-08-10', status: 'ISSUED', rejectionReason: null },
        { id: 'r2', memberId: 'm2', bookId: 'b2', copyId: 'b2-c2', startDate: '2026-08-03', returnDate: '2026-08-15', status: 'ISSUED', rejectionReason: null },
        // Confirmed (Visible on Calendar)
        { id: 'r3', memberId: 'm3', bookId: 'b3', copyId: 'b3-c2', startDate: '2026-08-12', returnDate: '2026-08-20', status: 'CONFIRMED', rejectionReason: null },
        // Submitted (In Review Queue)
        { id: 'r4', memberId: 'm1', bookId: 'b4', copyId: null, startDate: '2026-08-05', returnDate: '2026-08-12', status: 'SUBMITTED', rejectionReason: null },
        { id: 'r5', memberId: 'm2', bookId: 'b3', copyId: null, startDate: '2026-08-06', returnDate: '2026-08-20', status: 'SUBMITTED', rejectionReason: null },
        // Rejected
        { id: 'r6', memberId: 'm3', bookId: 'b2', copyId: null, startDate: '2026-08-01', returnDate: '2026-08-05', status: 'REJECTED', rejectionReason: 'Member has overdue fines' },
        // Returned
        { id: 'r7', memberId: 'm1', bookId: 'b2', copyId: 'b2-c1', startDate: '2026-07-20', returnDate: '2026-07-30', status: 'RETURNED', rejectionReason: null },
        // Cancelled
        { id: 'r8', memberId: 'm2', bookId: 'b1', copyId: null, startDate: '2026-08-02', returnDate: '2026-08-10', status: 'CANCELLED', rejectionReason: null }
    ]
};
