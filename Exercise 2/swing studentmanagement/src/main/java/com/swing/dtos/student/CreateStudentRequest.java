package com.swing.dtos.student;

import com.swing.exceptions.InvalidInputsException;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
public class CreateStudentRequest {
    private String name;
    private Double score;
    private String image;
    private String address;
    private String note;

    public InvalidInputsException validate() {
        Map<String, String> errors = new HashMap<>();

        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Name is required.");
        }
        if (score == null || score < 0 || score > 10) {
            errors.put("score", "Score must be between 0 and 10.");
        }
        if (address == null || address.length() > 255) {
            errors.put("address", "Address must not exceed 255 characters.");
        }
        if (note == null || note.length() > 500) {
            errors.put("note", "Note must not exceed 500 characters.");
        }

        if (!errors.isEmpty()) {
            return new InvalidInputsException(errors);
        }
        return null;
    }

}

