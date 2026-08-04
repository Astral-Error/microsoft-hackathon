package com.example.libraryms.model;

public class BookTitle {

    private final Long id;
    private final String title;
    private final String category;
    private final int totalCopies;

    public BookTitle(Long id, String title, String category, int totalCopies) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.totalCopies = totalCopies;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public int getTotalCopies() {
        return totalCopies;
    }
}