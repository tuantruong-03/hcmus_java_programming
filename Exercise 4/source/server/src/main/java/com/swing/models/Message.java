package com.swing.models;


import lombok.*;

import java.util.Date;

@Builder
@Getter
public class Message {
    private String id;
    private String chatRoomId;
    private Content content;
    private String senderId;
    private Date createdAt;
    private Date updatedAt;

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        @Setter
        private String value;
        private Type type;
        public enum Type {
            FILE, TEXT
        }
    }
}
