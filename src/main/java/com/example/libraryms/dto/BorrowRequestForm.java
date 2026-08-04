package com.example.libraryms.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BorrowRequestForm {

    @NotBlank(message = "Member ID is required.")
    private String memberId;

    @NotBlank(message = "Member name is required.")
    private String memberName;

    @NotNull(message = "Please select a book title.")
    private Long bookTitleId;

    @NotNull(message = "Start date is required.")
    @FutureOrPresent(message = "Start date cannot be in the past.")
    private LocalDate startDate;

    @NotNull(message = "Expected return date is required.")
    @FutureOrPresent(message = "Expected return date cannot be in the past.")
    private LocalDate expectedReturnDate;

    @Size(max = 500, message = "Reason must be 500 characters or less.")
    private String reason;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Long getBookTitleId() {
        return bookTitleId;
    }

    public void setBookTitleId(Long bookTitleId) {
        this.bookTitleId = bookTitleId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(LocalDate expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}