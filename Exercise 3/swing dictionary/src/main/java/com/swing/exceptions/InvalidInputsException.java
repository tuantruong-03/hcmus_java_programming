package com.swing.exceptions;

import lombok.Getter;

import java.util.Map;


@Getter
public class InvalidInputsException extends RuntimeException {
    private final Map<String, String> fieldAndError;

    public InvalidInputsException(Map<String, String> fieldAndError) {
        super("Invalid input fields.");
        this.fieldAndError = fieldAndError;
    }

}