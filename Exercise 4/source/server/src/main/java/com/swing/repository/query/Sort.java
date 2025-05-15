package com.swing.repository.query;

import lombok.Builder;

@Builder
public class Sort {
    private String field;
    private boolean  isAscending;

    public String prepareStatement() {
        return field + (isAscending ? " ASC" : " DESC");
    }
}