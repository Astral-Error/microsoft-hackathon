package com.example.libraryms.model;

import java.time.LocalDate;

public class BorrowRequest {

    private final Long id;
    private final String memberId;
    private final String memberName;
    private final Long titleId;
    private final Long copyId;
    private final String copyCode;
    private final LocalDate startDate;
    private final LocalDate expectedReturnDate;
    private final String reason;
    private RequestStatus status;
    private String reviewNote;

    public BorrowRequest(Long id, String memberId, String memberName, Long titleId, Long copyId, String copyCode,
            LocalDate startDate, LocalDate expectedReturnDate, String reason, RequestStatus status) {
        this.id = id;
        this.memberId = memberId;
        this.memberName = memberName;
        this.titleId = titleId;
        this.copyId = copyId;
        this.copyCode = copyCode;
        this.startDate = startDate;
        this.expectedReturnDate = expectedReturnDate;
        this.reason = reason;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public Long getTitleId() {
        return titleId;
    }

    public Long getCopyId() {
        return copyId;
    }

    public String getCopyCode() {
        return copyCode;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public String getReason() {
        return reason;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getReviewNote() {
        return reviewNote;
    }

    public void setReviewNote(String reviewNote) {
        this.reviewNote = reviewNote;
    }
}