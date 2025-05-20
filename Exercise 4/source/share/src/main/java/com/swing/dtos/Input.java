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
public class Input<T> {
    private Command command;
    private Map<String, String> metadata;
    private T body;

    public enum Command {
        LOGIN,
        REGISTER,
        SEND_MESSAGE,
        CREATE_CHATROOM,
        DISCONNECT,
    }
}