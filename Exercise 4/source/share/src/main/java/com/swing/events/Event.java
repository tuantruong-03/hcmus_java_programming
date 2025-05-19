package com.swing.events;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Event {
    private Type type;


    public enum Type {
        SEND_MESSAGE,
        DISCONNECT,
    }
}
