package com.swing.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Request<T> {
    private UseCase useCase;
    private Map<String, String> metadata;
    private T body;

    public enum UseCase {
        LOGIN,
        REGISTER,
    }
}
