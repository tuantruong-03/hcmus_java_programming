package com.swing.dtos.favorite;

import com.swing.exceptions.InvalidInputsException;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;


@Builder
@Getter
public class CreateFavoriteRequest {
    private String word;
    private String meaning;
    private String language;

    public InvalidInputsException validate() {
        Map<String, String> errors = new HashMap<>();

        if (word == null || word.trim().isEmpty()) {
            errors.put("word", "Word is required.");
        }
        if (meaning == null || meaning.trim().isEmpty()) {
            errors.put("meaning", "Meaning is required.");
        }
        if (language == null || language.trim().isEmpty()) {
            errors.put("language", "Language is required.");
        }

        if (!errors.isEmpty()) {
            return new InvalidInputsException(errors);
        }
        return null;
    }
}
