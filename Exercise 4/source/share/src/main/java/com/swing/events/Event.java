package com.swing.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
public class Event {
    private Type type;
    private Object payload;

    public Event(Type type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public enum Type {
        USER_LOGIN,
        USER_LOGOUT,
        SEND_MESSAGE,
        DELETE_MESSAGE,
        UPDATE_MESSAGE,
    }

    @Getter
    @NoArgsConstructor
    public static class LoginPayload {
        private String clientId;
        private String userId;
        private String name;
        private String username;

        public LoginPayload(String clientId, String userId, String name, String username) {
            this.clientId = clientId;
            this.name = name;
            this.userId = userId;
            this.username = username;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendMessagePayload {
        private String messageId;
        private String chatRoomId;
        private MessageContent content;
        private String senderId;
        private String senderName;
        private List<String> receiverIds;
        private Date createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteMessagePayload {
        private String messageId;
        private String chatRoomId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMessagePayload {
        private String messageId;
        private String chatRoomId;
        private MessageContent content;
    }
}
