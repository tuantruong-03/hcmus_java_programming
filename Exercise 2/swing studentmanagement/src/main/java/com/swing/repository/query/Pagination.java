package com.swing.repository.query;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Pagination {
    private int page = 0;
    private int size = 100;
    private Sort sort;

}
