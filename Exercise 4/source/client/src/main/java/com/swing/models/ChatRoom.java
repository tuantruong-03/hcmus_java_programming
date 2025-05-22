package com.swing.models;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoom {
    private String id;
    private String name;
    private String senderId;
    private String receiverId;
}
