package com.swing.models;


import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String id;
    private String chatRoomId;
    private Content content;
    private String senderId;
    private String senderName;
    private List<String> receiverIds;
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
            FILE, TEXT
        }
    }
}
