package com.swing.dtos.wordlookup;


import com.swing.exceptions.InvalidInputsException;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
public class CreateWordLookupRequest {
    private String word;
    private String language;
    private Long timestamp;

    public InvalidInputsException validate() {
        Map<String, String> errors = new HashMap<>();

        if (word == null || word.trim().isEmpty()) {
            errors.put("word", "Word is required.");
        }
        if (language == null || language.trim().isEmpty()) {
            errors.put("language", "Language is required.");
        }
        if (timestamp < 0 || timestamp > System.currentTimeMillis()) {
            timestamp = new Date().getTime();
        }
        if (!errors.isEmpty()) {
            return new InvalidInputsException(errors);
        }
        return null;
    }
}