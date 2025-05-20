package com.swing.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Getter
public class Message {
    private String id;
    private String chatRoomId;
    private Content content;
    private String senderId;
    private String senderAvatar;
    private Date createdAt;
    private Date updatedAt;

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        private String value;
        private Type type;
        public enum Type {
            FILE, STRING
        }
    }
}
