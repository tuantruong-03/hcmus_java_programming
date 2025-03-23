package com.swing.repository.query;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Query {
    private Where filter;
    private Pagination pagination;
    public static Query defaultInstance() {
        Sort sort = Sort.builder().ascending(Boolean.TRUE).build();
        return Query.builder()
                .pagination(Pagination.builder().page(0).size(100).sort(sort).build())
                .build();
    }
}