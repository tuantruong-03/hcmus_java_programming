package com.swing.repository.student;

import com.swing.repository.pagination.Pagination;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StudentQuery  {
    private String search;
    private Filter filter;
    private Pagination pagination;

    private static final StudentQuery DEFAULT_INSTANCE = StudentQuery.builder()
            .filter(Filter.builder().id(null).name(null).build())
            .pagination(Pagination.defaultInstance()) // Assuming Pagination has a default instance
            .build();

    public static StudentQuery defaultInstance() {
        return DEFAULT_INSTANCE;
    }

    @Getter
    @Builder
    public static class Filter {
        private Long id;
        private String name;
    }
}