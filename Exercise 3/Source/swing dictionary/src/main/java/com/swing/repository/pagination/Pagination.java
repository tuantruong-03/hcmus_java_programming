package com.swing.repository.pagination;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Pagination {
    private int page = 0;
    private int size = 100;
    private Sort sort;

    private static final Pagination DEFAULT_INSTANCE = Pagination.builder()
            .page(0)
            .size(100)
            .sort(Sort.defaultInstance()) // Assuming Sort has a default instance
            .build();

    public static Pagination defaultInstance() {
        return DEFAULT_INSTANCE;
    }

}
