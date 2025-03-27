package com.swing.dtos.student;


import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Builder
@Getter
public class FilterStudentsRequest {
    private String search;
    private Integer page;
    private Integer size;
    private String sortField;
    private String sortOrder;

    public static CustomBuilder customBuilder() {
        return new CustomBuilder();
    }

    public static class CustomBuilder extends FilterStudentsRequestBuilder {
        @Override
        public FilterStudentsRequest build() {
            // Default values if null
            if (super.page == null) {
                super.page(0);
            }
            if (super.size == null || super.size <= 0) {
                super.size(10);
            }
            if (!Objects.equals(super.sortOrder, "ASC") && !Objects.equals(super.sortOrder, "DESC")) {
                super.sortOrder = "ASC";
            }
            return super.build();
        }
    }

}
