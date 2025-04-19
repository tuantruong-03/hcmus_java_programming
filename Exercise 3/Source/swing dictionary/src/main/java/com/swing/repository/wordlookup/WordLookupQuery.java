package com.swing.repository.wordlookup;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WordLookupQuery {
    private Long fromTimeInMilSec;
    private Long toTimeInMilSec;
    private String language;
}
