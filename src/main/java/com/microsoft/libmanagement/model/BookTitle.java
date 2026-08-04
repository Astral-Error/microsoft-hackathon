package com.microsoft.libmanagement.model;

import java.util.ArrayList;
import java.util.List;

public class BookTitle {
    private Long id;
    private String title;
    private String category;
    private int totalCopies;
    private final List<BookCopy> copies = new ArrayList<>();

    public BookTitle() {}

    public BookTitle(Long id, String title, String category, int totalCopies) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.totalCopies = totalCopies;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }
    public List<BookCopy> getCopies() { return copies; }
}
