package com.swing.repository.query;


public class Sort {
    private final String field;
    private final boolean  isAscending;

    public Sort(String field, boolean isAscending) {
        this.field = field;
        this.isAscending = isAscending;
    }

    public String prepareStatement() {
        return field + (isAscending ? " ASC" : " DESC");
    }
}