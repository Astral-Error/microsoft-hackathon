package com.microsoft.libmanagement.model;

import java.time.LocalDate;

public class BorrowRequest {
    private Long id;
    private Long memberId;
    private String bookTitle;
    private LocalDate startDate;
    private LocalDate expectedReturnDate;
    private RequestStatus status;
    private Long assignedCopyId;
    private String reviewComment;

    public BorrowRequest() {}

    public BorrowRequest(Long id, Long memberId, String bookTitle, LocalDate startDate, LocalDate expectedReturnDate) {
        this.id = id;
        this.memberId = memberId;
        this.bookTitle = bookTitle;
        this.startDate = startDate;
        this.expectedReturnDate = expectedReturnDate;
        this.status = RequestStatus.PENDING_REVIEW;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getExpectedReturnDate() { return expectedReturnDate; }
    public void setExpectedReturnDate(LocalDate expectedReturnDate) { this.expectedReturnDate = expectedReturnDate; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    public Long getAssignedCopyId() { return assignedCopyId; }
    public void setAssignedCopyId(Long assignedCopyId) { this.assignedCopyId = assignedCopyId; }
    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }
}
