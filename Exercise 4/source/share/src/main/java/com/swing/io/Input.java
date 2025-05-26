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
        KEEP_CONNECTION_ALIVE,

        GET_MY_PROFILE,

        CREATE_CHATROOM,
        GET_MY_CHAT_ROOMS,
        GET_CHAT_ROOM,
        CHECK_CHAT_ROOM_EXISTENCE,

        GET_MESSAGES,
        SEND_MESSAGE,
        DELETE_MESSAGE,
        UPDATE_MESSAGE,
    }
}