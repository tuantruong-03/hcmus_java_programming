package com.swing.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageContent {
    private String value;
    private Type type;

    public enum Type {
        FILE, TEXT
    }
}