package com.swing.io;

import lombok.*;

import java.util.Map;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Input<T> {
    private Command command;
    private Map<String, String> metadata;

    private T body;

    public enum Command {
        REGISTER,
        LOGIN,
        LOGOUT,
        DISCONNECT,
        CREATE_CHATROOM,
        GET_MY_PROFILE,
        GET_MY_CHAT_ROOMS,
        GET_CHAT_ROOM,
        GET_MESSAGES,
        KEEP_CONNECTION_ALIVE,
        SEND_MESSAGE,
    }
}