package com.swingchat.repository.query;

import lombok.Builder;

@Builder
public class Sort {
    private String field;
    private boolean  isAscending;

    public String toStatement() {
        return field + (isAscending ? " ASC" : " DESC");
    }
}