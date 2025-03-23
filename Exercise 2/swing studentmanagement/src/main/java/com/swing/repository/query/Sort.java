package com.swing.repository.query;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Sort {
    private String field;
    private Boolean ascending = true ;
}