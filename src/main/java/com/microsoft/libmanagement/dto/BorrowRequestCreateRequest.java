package com.microsoft.libmanagement.dto;

import java.time.LocalDate;

public record BorrowRequestCreateRequest(Long memberId, String bookTitle, LocalDate startDate, LocalDate expectedReturnDate) {
}
