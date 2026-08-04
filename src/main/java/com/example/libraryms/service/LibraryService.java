package com.example.libraryms.service;

import com.example.libraryms.dto.BorrowRequestForm;
import com.example.libraryms.exception.BusinessRuleException;
import com.example.libraryms.model.BookCopy;
import com.example.libraryms.model.BookTitle;
import com.example.libraryms.model.BorrowRequest;
import com.example.libraryms.model.CopyCondition;
import com.example.libraryms.model.RequestStatus;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class LibraryService {

    private final Clock clock;
    private final Map<Long, BookTitle> titles = new HashMap<>();
    private final Map<Long, BookCopy> copies = new HashMap<>();
    private final Map<Long, BorrowRequest> requests = new HashMap<>();
    private final AtomicLong titleSeq = new AtomicLong(1);
    private final AtomicLong copySeq = new AtomicLong(1);
    private final AtomicLong requestSeq = new AtomicLong(1);

    public LibraryService(Clock clock) {
        this.clock = clock;
    }

    @PostConstruct
    void seed() {
        addTitle("Clean Code", "Software Engineering", 4, "CC");
        addTitle("Introduction to Algorithms", "Algorithms", 3, "ALG");
        addTitle("Design Patterns", "Software Design", 2, "DP");
        addTitle("Database System Concepts", "Databases", 5, "DB");
        addTitle("Artificial Intelligence: A Modern Approach", "Artificial Intelligence", 3, "AI");

        issueInitialCopy(1L, "CC-1", LocalDate.of(2026, 7, 20), LocalDate.of(2026, 8, 4), "Issued to student batch A");
        issueInitialCopy(1L, "CC-2", LocalDate.of(2026, 7, 18), LocalDate.of(2026, 8, 4), "Issued to student batch B");
        reserveInitialCopy(2L, "ALG-1", LocalDate.of(2026, 8, 4), LocalDate.of(2026, 8, 8), "Reserved for placement prep");
        markCopyUnavailable(3L, "DP-1", "Damaged and awaiting repair");
        issueInitialCopy(5L, "AI-1", LocalDate.of(2026, 7, 25), LocalDate.of(2026, 8, 4), "Issued to project team");
        reserveInitialCopy(5L, "AI-2", LocalDate.of(2026, 8, 6), LocalDate.of(2026, 8, 7), "Reserved for research use");
    }

    public DashboardView getDashboard() {
        LocalDate today = LocalDate.now(clock);
        List<TitleView> titleViews = titles.values().stream()
                .sorted(Comparator.comparing(BookTitle::getTitle))
                .map(title -> toTitleView(title, today))
                .collect(Collectors.toList());

        List<RequestView> requestViews = requests.values().stream()
                .sorted(Comparator.comparing(BorrowRequest::getId).reversed())
                .map(this::toRequestView)
                .collect(Collectors.toList());

        long openRequests = requests.values().stream()
                .filter(request -> request.getStatus() == RequestStatus.REQUESTED)
                .count();
        long approvedReservations = requests.values().stream()
                .filter(request -> request.getStatus() == RequestStatus.APPROVED_RESERVED)
                .count();
        long activeLoans = requests.values().stream()
                .filter(request -> request.getStatus() == RequestStatus.ISSUED)
                .count();
        long unavailableCopies = copies.values().stream()
                .filter(copy -> copy.getCondition() == CopyCondition.UNAVAILABLE)
                .count();

        DashboardStats stats = new DashboardStats(titles.size(), copies.size(), openRequests, approvedReservations,
                activeLoans, unavailableCopies);

        return new DashboardView(today, stats, titleViews, requestViews);
    }

    public BorrowRequest submitBorrowingRequest(BorrowRequestForm form) {
        validateForm(form);
        BookTitle title = title(form.getBookTitleId());
        BookCopy copy = findAvailableCopy(title.getId(), form.getStartDate(), form.getExpectedReturnDate())
                .orElseThrow(() -> new BusinessRuleException("No copy is available for the requested period."));

        Long id = requestSeq.getAndIncrement();
        BorrowRequest request = new BorrowRequest(id, form.getMemberId(), form.getMemberName(), title.getId(),
                copy.getId(), copy.getCopyCode(), form.getStartDate(), form.getExpectedReturnDate(), form.getReason(),
                RequestStatus.REQUESTED);
        requests.put(id, request);
        return request;
    }

    public void approveRequest(Long requestId) {
        BorrowRequest request = request(requestId);
        ensureStatus(request, RequestStatus.REQUESTED, "Only submitted requests can be approved.");
        request.setStatus(RequestStatus.APPROVED_RESERVED);
        request.setReviewNote("Approved as confirmed reservation.");
    }

    public void rejectRequest(Long requestId) {
        BorrowRequest request = request(requestId);
        ensureStatus(request, RequestStatus.REQUESTED, "Only submitted requests can be rejected.");
        request.setStatus(RequestStatus.REJECTED);
        request.setReviewNote("Rejected during review.");
    }

    public void issueRequest(Long requestId) {
        BorrowRequest request = request(requestId);
        ensureStatus(request, RequestStatus.APPROVED_RESERVED, "Only approved reservations can be issued.");
        if (LocalDate.now(clock).isBefore(request.getStartDate())) {
            throw new BusinessRuleException("The borrowing start date has not been reached yet.");
        }
        request.setStatus(RequestStatus.ISSUED);
        request.setReviewNote("Issued to member.");
    }

    public void cancelReservation(Long requestId) {
        BorrowRequest request = request(requestId);
        ensureStatus(request, RequestStatus.APPROVED_RESERVED, "Only confirmed reservations can be cancelled.");
        request.setStatus(RequestStatus.CANCELLED);
        request.setReviewNote("Cancelled before issue.");
    }

    public void returnBook(Long requestId, boolean markUnavailable, String note) {
        BorrowRequest request = request(requestId);
        ensureStatus(request, RequestStatus.ISSUED, "Only issued books can be returned.");
        request.setStatus(RequestStatus.RETURNED);
        request.setReviewNote(markUnavailable ? "Returned and marked unavailable." : "Returned successfully.");
        if (markUnavailable) {
            BookCopy copy = copy(request.getCopyId());
            copy.setCondition(CopyCondition.UNAVAILABLE);
            copy.setConditionNote(note == null || note.isBlank() ? "Marked unavailable during return" : note);
        }
    }

    private BookTitle title(Long id) {
        BookTitle title = titles.get(id);
        if (title == null) {
            throw new BusinessRuleException("Unknown book title selected.");
        }
        return title;
    }

    private BookCopy copy(Long id) {
        BookCopy copy = copies.get(id);
        if (copy == null) {
            throw new BusinessRuleException("Unknown book copy.");
        }
        return copy;
    }

    private BorrowRequest request(Long id) {
        BorrowRequest request = requests.get(id);
        if (request == null) {
            throw new BusinessRuleException("Request not found.");
        }
        return request;
    }

    private void ensureStatus(BorrowRequest request, RequestStatus expectedStatus, String message) {
        if (request.getStatus() != expectedStatus) {
            throw new BusinessRuleException(message);
        }
    }

    private void validateForm(BorrowRequestForm form) {
        if (form.getMemberId() == null || form.getMemberId().isBlank()) {
            throw new BusinessRuleException("Member ID is required.");
        }
        if (form.getMemberName() == null || form.getMemberName().isBlank()) {
            throw new BusinessRuleException("Member name is required.");
        }
        if (form.getBookTitleId() == null) {
            throw new BusinessRuleException("Please select a book title.");
        }
        if (form.getStartDate() == null || form.getExpectedReturnDate() == null) {
            throw new BusinessRuleException("Start date and expected return date are required.");
        }
        if (form.getExpectedReturnDate().isBefore(form.getStartDate())) {
            throw new BusinessRuleException("Expected return date must be on or after the start date.");
        }
    }

    private Optional<BookCopy> findAvailableCopy(Long titleId, LocalDate startDate, LocalDate endDate) {
        return copies.values().stream()
                .filter(copy -> copy.getTitleId().equals(titleId))
                .filter(copy -> copy.getCondition() == CopyCondition.AVAILABLE)
                .filter(copy -> !hasOverlappingCommitment(copy.getId(), startDate, endDate))
                .sorted(Comparator.comparing(BookCopy::getCopyCode))
                .findFirst();
    }

    private boolean hasOverlappingCommitment(Long copyId, LocalDate startDate, LocalDate endDate) {
        return requests.values().stream()
                .filter(request -> request.getCopyId().equals(copyId))
                .filter(request -> isActive(request.getStatus()))
                .anyMatch(request -> overlaps(startDate, endDate, request.getStartDate(), request.getExpectedReturnDate()));
    }

    private boolean isActive(RequestStatus status) {
        return status == RequestStatus.REQUESTED || status == RequestStatus.APPROVED_RESERVED
                || status == RequestStatus.ISSUED;
    }

    private boolean overlaps(LocalDate requestedStart, LocalDate requestedEnd, LocalDate existingStart,
            LocalDate existingEnd) {
        return !requestedEnd.isBefore(existingStart) && !requestedStart.isAfter(existingEnd);
    }

    private TitleView toTitleView(BookTitle title, LocalDate today) {
        List<CopyView> copyViews = copies.values().stream()
                .filter(copy -> copy.getTitleId().equals(title.getId()))
                .sorted(Comparator.comparing(BookCopy::getCopyCode))
                .map(copy -> toCopyView(copy, today))
                .collect(Collectors.toList());

        long availableCount = copyViews.stream().filter(CopyView::availableNow).count();
        return new TitleView(title, copyViews, availableCount);
    }

    private CopyView toCopyView(BookCopy copy, LocalDate today) {
        Optional<BorrowRequest> activeRequest = requests.values().stream()
                .filter(request -> request.getCopyId().equals(copy.getId()))
                .filter(request -> isActive(request.getStatus()))
                .filter(request -> !today.isBefore(request.getStartDate()) && !today.isAfter(request.getExpectedReturnDate()))
                .findFirst();

        String activity = activeRequest.map(request -> request.getStatus().name() + " " + request.getStartDate() + " to "
                + request.getExpectedReturnDate()).orElse("No active commitment today");

        return new CopyView(copy.getCopyCode(), copy.getCondition(), copy.getConditionNote(), activity,
                copy.getCondition() == CopyCondition.AVAILABLE && activeRequest.isEmpty());
    }

    private RequestView toRequestView(BorrowRequest request) {
        BookTitle title = title(request.getTitleId());
        return new RequestView(request.getId(), request.getMemberId(), request.getMemberName(), title.getTitle(),
                request.getCopyCode(), request.getStartDate(), request.getExpectedReturnDate(), request.getReason(),
                request.getStatus(), request.getReviewNote(), request.getStatus() == RequestStatus.REQUESTED,
                request.getStatus() == RequestStatus.REQUESTED, request.getStatus() == RequestStatus.APPROVED_RESERVED,
                request.getStatus() == RequestStatus.APPROVED_RESERVED, request.getStatus() == RequestStatus.ISSUED,
                request.getStatus() == RequestStatus.ISSUED);
    }

    private Long addTitle(String title, String category, int totalCopies, String copyPrefix) {
        Long id = titleSeq.getAndIncrement();
        titles.put(id, new BookTitle(id, title, category, totalCopies));
        for (int i = 1; i <= totalCopies; i++) {
            Long copyId = copySeq.getAndIncrement();
            copies.put(copyId, new BookCopy(copyId, id, copyPrefix + "-" + i, CopyCondition.AVAILABLE, null));
        }
        return id;
    }

    private void issueInitialCopy(Long titleId, String copyCode, LocalDate startDate, LocalDate endDate, String note) {
        BookCopy copy = copyForTitle(titleId, copyCode);
        BorrowRequest request = new BorrowRequest(requestSeq.getAndIncrement(), "seed", "Seed Data", titleId,
                copy.getId(), copyCode, startDate, endDate, note, RequestStatus.ISSUED);
        request.setReviewNote(note);
        requests.put(request.getId(), request);
    }

    private void reserveInitialCopy(Long titleId, String copyCode, LocalDate startDate, LocalDate endDate, String note) {
        BookCopy copy = copyForTitle(titleId, copyCode);
        BorrowRequest request = new BorrowRequest(requestSeq.getAndIncrement(), "seed", "Seed Data", titleId,
                copy.getId(), copyCode, startDate, endDate, note, RequestStatus.APPROVED_RESERVED);
        request.setReviewNote(note);
        requests.put(request.getId(), request);
    }

    private void markCopyUnavailable(Long titleId, String copyCode, String note) {
        BookCopy copy = copyForTitle(titleId, copyCode);
        copy.setCondition(CopyCondition.UNAVAILABLE);
        copy.setConditionNote(note);
    }

    private BookCopy copyForTitle(Long titleId, String copyCode) {
        return copies.values().stream()
                .filter(copy -> copy.getTitleId().equals(titleId))
                .filter(copy -> copy.getCopyCode().equals(copyCode))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("Seed copy not found: " + copyCode));
    }

    public record DashboardView(LocalDate today, DashboardStats stats, List<TitleView> titles,
            List<RequestView> requests) {
    }

    public record DashboardStats(int titleCount, int copyCount, long openRequests, long reservations, long loans,
            long unavailableCopies) {
    }

    public record TitleView(BookTitle title, List<CopyView> copies, long availableNow) {
    }

    public record CopyView(String copyCode, CopyCondition condition, String conditionNote, String activity,
            boolean availableNow) {
    }

    public record RequestView(Long id, String memberId, String memberName, String title, String copyCode,
            LocalDate startDate, LocalDate expectedReturnDate, String reason, RequestStatus status, String reviewNote,
            boolean canApprove, boolean canReject, boolean canIssue, boolean canCancel, boolean canReturn,
            boolean canReturnUnavailable) {
    }
}