package com.swing.dtos.record;


import com.swing.exceptions.InvalidInputsException;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
public class CreateRecordRequest {
    private String word;
    private String meaning;

    public InvalidInputsException validate() {
        Map<String, String> errors = new HashMap<>();

        if (word == null || word.trim().isEmpty()) {
            errors.put("word", "Word is required.");
        }
        if (meaning == null || meaning.trim().isEmpty()) {
            errors.put("meaning", "Meaning is required.");
        }

        if (!errors.isEmpty()) {
            return new InvalidInputsException(errors);
        }
        return null;
    }
}