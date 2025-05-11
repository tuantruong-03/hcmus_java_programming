package com.swingchat.models;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Chat {
    private String id;
    private String name;
    private String senderId;
    private String receiverId;
    private String message;
}
