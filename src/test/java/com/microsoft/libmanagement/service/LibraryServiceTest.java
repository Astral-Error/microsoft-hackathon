package com.microsoft.libmanagement.service;

import com.microsoft.libmanagement.dto.BorrowRequestCreateRequest;
import com.microsoft.libmanagement.dto.BorrowRequestResponse;
import com.microsoft.libmanagement.model.RequestStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LibraryServiceTest {

    private final LibraryService service = new LibraryService();

    @Test
    void shouldRejectBorrowRequestWhenNoCopyIsAvailableForRequestedPeriod() {
        BorrowRequestCreateRequest firstRequest = new BorrowRequestCreateRequest(
                1L,
                "Design Patterns",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 3)
        );
        service.submitBorrowRequest(firstRequest);

        BorrowRequestCreateRequest secondRequest = new BorrowRequestCreateRequest(
                2L,
                "Design Patterns",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 3)
        );

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.submitBorrowRequest(secondRequest));

        assertTrue(exception.getMessage().contains("No available copy"));
    }

    @Test
    void shouldMoveRequestFromReviewToReservationAndThenToLoanFlow() {
        BorrowRequestCreateRequest request = new BorrowRequestCreateRequest(
                1L,
                "Database System Concepts",
                LocalDate.of(2026, 8, 10),
                LocalDate.of(2026, 8, 12)
        );

        BorrowRequestResponse submitted = service.submitBorrowRequest(request);
        assertEquals(RequestStatus.PENDING_REVIEW, submitted.status());

        BorrowRequestResponse approved = service.reviewRequest(submitted.id(), true, "Approved");
        assertEquals(RequestStatus.RESERVED, approved.status());

        BorrowRequestResponse issued = service.issueRequest(approved.id());
        assertEquals(RequestStatus.ISSUED, issued.status());

        BorrowRequestResponse returned = service.returnRequest(issued.id(), false);
        assertEquals(RequestStatus.RETURNED, returned.status());
    }
}
