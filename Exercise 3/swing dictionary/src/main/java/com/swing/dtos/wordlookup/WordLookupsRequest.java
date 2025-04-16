package com.swing.dtos.wordlookup;

import com.swing.exceptions.InvalidInputsException;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
public class WordLookupsRequest {
    private String language;
    private Long fromTime;
    private Long toTime;

    public InvalidInputsException validate() {
        Map<String, String> errors = new HashMap<>();
        if (language == null || language.trim().isEmpty()) {
            errors.put("language", "Language is required.");
        }
        if (fromTime > toTime || fromTime < 0) {
            errors.put("time", "To time must be greater than or equal to from time and can't be negative");

        }
        if (!errors.isEmpty()) {
            return new InvalidInputsException(errors);
        }
        return null;
    }
}
