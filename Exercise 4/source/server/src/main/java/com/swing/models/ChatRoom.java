package com.swing.models;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
public class ChatRoom {
    private String id;
    private String name;
    private Boolean isGroup;
    private Date createdAt;
    private Date updatedAt;
}
