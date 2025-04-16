package com.swing.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WordLookup {

    private String word;
    private String language;
    private Long timestamp;

    public WordLookup() {}
}
