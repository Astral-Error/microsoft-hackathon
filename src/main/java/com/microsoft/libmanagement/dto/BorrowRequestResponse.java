package com.microsoft.libmanagement.dto;

import com.microsoft.libmanagement.model.RequestStatus;

import java.time.LocalDate;

public record BorrowRequestResponse(Long id, Long memberId, String bookTitle, LocalDate startDate, LocalDate expectedReturnDate,
                                   RequestStatus status, Long assignedCopyId, String reviewComment) {
}
