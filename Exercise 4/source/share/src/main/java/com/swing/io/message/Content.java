package com.swing.io.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    private String value;
    private Type type;
    public enum Type {
        FILE, TEXT
    }
}