package com.swing.models;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChatRoom {
    private String id;
    private String name;
}
