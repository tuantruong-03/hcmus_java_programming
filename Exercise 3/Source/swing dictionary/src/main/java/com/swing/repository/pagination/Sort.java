package com.swing.repository.pagination;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Sort {
    private String field;
    private Boolean ascending = true ;

    private static final Sort DEFAULT_INSTANCE = Sort.builder()
            .ascending(true) // Default ascending order
            .build();

    public static Sort defaultInstance() {
        return DEFAULT_INSTANCE;
    }
}