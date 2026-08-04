package com.example.libraryms.model;

public class BookCopy {

    private final Long id;
    private final Long titleId;
    private final String copyCode;
    private CopyCondition condition;
    private String conditionNote;

    public BookCopy(Long id, Long titleId, String copyCode, CopyCondition condition, String conditionNote) {
        this.id = id;
        this.titleId = titleId;
        this.copyCode = copyCode;
        this.condition = condition;
        this.conditionNote = conditionNote;
    }

    public Long getId() {
        return id;
    }

    public Long getTitleId() {
        return titleId;
    }

    public String getCopyCode() {
        return copyCode;
    }

    public CopyCondition getCondition() {
        return condition;
    }

    public void setCondition(CopyCondition condition) {
        this.condition = condition;
    }

    public String getConditionNote() {
        return conditionNote;
    }

    public void setConditionNote(String conditionNote) {
        this.conditionNote = conditionNote;
    }
}