package com.microsoft.libmanagement.model;

import java.time.LocalDate;

public class BookCopy {
    private Long id;
    private Long titleId;
    private String copyNumber;
    private boolean available;
    private boolean damaged;
    private LocalDate unavailableFrom;
    private LocalDate unavailableTo;

    public BookCopy() {}

    public BookCopy(Long id, Long titleId, String copyNumber) {
        this.id = id;
        this.titleId = titleId;
        this.copyNumber = copyNumber;
        this.available = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTitleId() { return titleId; }
    public void setTitleId(Long titleId) { this.titleId = titleId; }
    public String getCopyNumber() { return copyNumber; }
    public void setCopyNumber(String copyNumber) { this.copyNumber = copyNumber; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public boolean isDamaged() { return damaged; }
    public void setDamaged(boolean damaged) { this.damaged = damaged; }
    public LocalDate getUnavailableFrom() { return unavailableFrom; }
    public void setUnavailableFrom(LocalDate unavailableFrom) { this.unavailableFrom = unavailableFrom; }
    public LocalDate getUnavailableTo() { return unavailableTo; }
    public void setUnavailableTo(LocalDate unavailableTo) { this.unavailableTo = unavailableTo; }
}
