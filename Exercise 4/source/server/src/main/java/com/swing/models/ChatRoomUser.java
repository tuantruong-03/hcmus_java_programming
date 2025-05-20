package com.swing.models;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChatRoomUser {
    private String chatRoomId;
    private String userId;
}
