package com.swing.io.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Getter
public class CreateMessageInput {
    private String chatRoomId;
    private Content content;
    private String senderId;
    private String receiverId;
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
