package com.swing.dtos.wordlookup;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WordLookupStats {
    private String word;
    private int count;


}
