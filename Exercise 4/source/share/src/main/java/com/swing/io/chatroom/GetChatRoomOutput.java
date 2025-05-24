package com.swing.io.chatroom;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Builder
@Getter
public class GetChatRoomOutput {
    private String id;
    private String name;
    private Map<String, String> members; // userId -> nickname
    private boolean isGroup;
    private Date createdAt;
    private Date updatedAt;
}
