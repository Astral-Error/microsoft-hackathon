package com.microsoft.libmanagement.service;

import com.microsoft.libmanagement.dto.BorrowRequestCreateRequest;
import com.microsoft.libmanagement.dto.BorrowRequestResponse;
import com.microsoft.libmanagement.model.BookCopy;
import com.microsoft.libmanagement.model.BookTitle;
import com.microsoft.libmanagement.model.BorrowRequest;
import com.microsoft.libmanagement.model.RequestStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class LibraryService {
    private final Map<Long, BookTitle> books = new LinkedHashMap<>();
    private final Map<Long, BorrowRequest> requests = new LinkedHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public LibraryService() {
        seedData();
    }

    public BorrowRequestResponse submitBorrowRequest(BorrowRequestCreateRequest request) {
        validateRequest(request);

        BookTitle title = findBookByTitle(request.bookTitle());
        BookCopy availableCopy = findAvailableCopy(title, request.startDate(), request.expectedReturnDate());
        if (availableCopy == null) {
            throw new IllegalStateException("No available copy for the selected book and requested period.");
        }

        BorrowRequest borrowRequest = new BorrowRequest(
                idGenerator.getAndIncrement(),
                request.memberId(),
                request.bookTitle(),
                request.startDate(),
                request.expectedReturnDate()
        );
        borrowRequest.setAssignedCopyId(availableCopy.getId());
        borrowRequest.setStatus(RequestStatus.PENDING_REVIEW);
        requests.put(borrowRequest.getId(), borrowRequest);

        return toResponse(borrowRequest);
    }

    public BorrowRequestResponse reviewRequest(Long requestId, boolean approved, String comment) {
        BorrowRequest request = requests.get(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Borrow request not found.");
        }
        if (request.getStatus() != RequestStatus.PENDING_REVIEW) {
            throw new IllegalStateException("Only pending review requests can be reviewed.");
        }
        if (approved) {
            request.setStatus(RequestStatus.RESERVED);
            request.setReviewComment(comment);
        } else {
            request.setStatus(RequestStatus.REJECTED);
            request.setReviewComment(comment);
            releaseAssignedCopy(request);
        }
        return toResponse(request);
    }

    public BorrowRequestResponse cancelRequest(Long requestId) {
        BorrowRequest request = requests.get(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Borrow request not found.");
        }
        if (request.getStatus() != RequestStatus.RESERVED) {
            throw new IllegalStateException("Only reserved requests can be cancelled.");
        }
        request.setStatus(RequestStatus.CANCELLED);
        releaseAssignedCopy(request);
        return toResponse(request);
    }

    public BorrowRequestResponse issueRequest(Long requestId) {
        BorrowRequest request = requests.get(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Borrow request not found.");
        }
        if (request.getStatus() != RequestStatus.RESERVED) {
            throw new IllegalStateException("Only reserved requests can be issued.");
        }
        request.setStatus(RequestStatus.ISSUED);
        return toResponse(request);
    }

    public BorrowRequestResponse returnRequest(Long requestId, boolean markUnavailable) {
        BorrowRequest request = requests.get(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Borrow request not found.");
        }
        if (request.getStatus() != RequestStatus.ISSUED) {
            throw new IllegalStateException("Only issued requests can be returned.");
        }
        request.setStatus(RequestStatus.RETURNED);
        if (markUnavailable) {
            BookCopy copy = findCopyById(request.getAssignedCopyId());
            if (copy != null) {
                copy.setAvailable(false);
                copy.setDamaged(true);
            }
        }
        return toResponse(request);
    }

    public List<BorrowRequestResponse> listRequests() {
        return requests.values().stream().map(this::toResponse).toList();
    }

    private void validateRequest(BorrowRequestCreateRequest request) {
        if (request.memberId() == null || request.bookTitle() == null || request.bookTitle().isBlank()) {
            throw new IllegalArgumentException("Member and book title are required.");
        }
        if (request.startDate() == null || request.expectedReturnDate() == null) {
            throw new IllegalArgumentException("Borrow and return dates are required.");
        }
        if (!request.expectedReturnDate().isAfter(request.startDate())) {
            throw new IllegalArgumentException("Return date must be after the borrow start date.");
        }
        if (findBookByTitle(request.bookTitle()) == null) {
            throw new IllegalArgumentException("Book title is not recognized.");
        }
    }

    private BookTitle findBookByTitle(String title) {
        return books.values().stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }

    private BookCopy findAvailableCopy(BookTitle title, LocalDate startDate, LocalDate endDate) {
        return title.getCopies().stream()
                .filter(BookCopy::isAvailable)
                .filter(copy -> !hasOperationalRestriction(copy, startDate, endDate))
                .filter(copy -> !hasActiveCommitment(copy, startDate, endDate))
                .findFirst()
                .orElse(null);
    }

    private boolean hasOperationalRestriction(BookCopy copy, LocalDate startDate, LocalDate endDate) {
        if (copy.isDamaged()) {
            return true;
        }
        if (copy.getUnavailableFrom() != null && copy.getUnavailableTo() != null) {
            return !(endDate.isBefore(copy.getUnavailableFrom()) || startDate.isAfter(copy.getUnavailableTo()));
        }
        return false;
    }

    private boolean hasActiveCommitment(BookCopy copy, LocalDate startDate, LocalDate endDate) {
        return requests.values().stream()
                .filter(request -> request.getAssignedCopyId() != null && request.getAssignedCopyId().equals(copy.getId()))
                .filter(request -> request.getStatus() == RequestStatus.PENDING_REVIEW
                        || request.getStatus() == RequestStatus.RESERVED
                        || request.getStatus() == RequestStatus.ISSUED)
                .anyMatch(request -> !(endDate.isBefore(request.getStartDate()) || startDate.isAfter(request.getExpectedReturnDate())));
    }

    private void releaseAssignedCopy(BorrowRequest request) {
        if (request.getAssignedCopyId() == null) {
            return;
        }
        request.setAssignedCopyId(null);
    }

    private BookCopy findCopyById(Long id) {
        return books.values().stream()
                .flatMap(book -> book.getCopies().stream())
                .filter(copy -> copy.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private BorrowRequestResponse toResponse(BorrowRequest request) {
        return new BorrowRequestResponse(
                request.getId(),
                request.getMemberId(),
                request.getBookTitle(),
                request.getStartDate(),
                request.getExpectedReturnDate(),
                request.getStatus(),
                request.getAssignedCopyId(),
                request.getReviewComment()
        );
    }

    private void seedData() {
        BookTitle cleanCode = new BookTitle(1L, "Clean Code", "Software Engineering", 4);
        BookCopy cleanCodeA = new BookCopy(101L, 1L, "A");
        cleanCodeA.setUnavailableFrom(LocalDate.of(2026, 8, 1));
        cleanCodeA.setUnavailableTo(LocalDate.of(2026, 8, 4));
        cleanCode.getCopies().add(cleanCodeA);
        BookCopy cleanCodeB = new BookCopy(102L, 1L, "B");
        cleanCodeB.setUnavailableFrom(LocalDate.of(2026, 8, 1));
        cleanCodeB.setUnavailableTo(LocalDate.of(2026, 8, 4));
        cleanCode.getCopies().add(cleanCodeB);
        cleanCode.getCopies().add(new BookCopy(103L, 1L, "C"));
        cleanCode.getCopies().add(new BookCopy(104L, 1L, "D"));
        books.put(cleanCode.getId(), cleanCode);

        BookTitle algorithms = new BookTitle(2L, "Introduction to Algorithms", "Algorithms", 3);
        BookCopy algoA = new BookCopy(201L, 2L, "A");
        algoA.setUnavailableFrom(LocalDate.of(2026, 8, 4));
        algoA.setUnavailableTo(LocalDate.of(2026, 8, 8));
        algorithms.getCopies().add(algoA);
        algorithms.getCopies().add(new BookCopy(202L, 2L, "B"));
        algorithms.getCopies().add(new BookCopy(203L, 2L, "C"));
        books.put(algorithms.getId(), algorithms);

        BookTitle patterns = new BookTitle(3L, "Design Patterns", "Software Design", 2);
        patterns.getCopies().add(new BookCopy(301L, 3L, "A"));
        BookCopy patternDamaged = new BookCopy(302L, 3L, "B");
        patternDamaged.setDamaged(true);
        patternDamaged.setAvailable(false);
        patterns.getCopies().add(patternDamaged);
        books.put(patterns.getId(), patterns);

        BookTitle db = new BookTitle(4L, "Database System Concepts", "Databases", 5);
        db.getCopies().add(new BookCopy(401L, 4L, "A"));
        db.getCopies().add(new BookCopy(402L, 4L, "B"));
        db.getCopies().add(new BookCopy(403L, 4L, "C"));
        db.getCopies().add(new BookCopy(404L, 4L, "D"));
        db.getCopies().add(new BookCopy(405L, 4L, "E"));
        books.put(db.getId(), db);

        BookTitle ai = new BookTitle(5L, "Artificial Intelligence: A Modern Approach", "Artificial Intelligence", 3);
        BookCopy aiA = new BookCopy(501L, 5L, "A");
        aiA.setUnavailableFrom(LocalDate.of(2026, 8, 1));
        aiA.setUnavailableTo(LocalDate.of(2026, 8, 4));
        ai.getCopies().add(aiA);
        BookCopy aiB = new BookCopy(502L, 5L, "B");
        aiB.setUnavailableFrom(LocalDate.of(2026, 8, 6));
        aiB.setUnavailableTo(LocalDate.of(2026, 8, 7));
        ai.getCopies().add(aiB);
        ai.getCopies().add(new BookCopy(503L, 5L, "C"));
        books.put(ai.getId(), ai);
    }
}
