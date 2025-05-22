package com.swing.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Builder
@Getter
public class Event {
    private Type type;
    private Object payload;

    public Event(Type type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public enum Type {
        LOGIN,
        LOGOUT,
        SEND_MESSAGE,
    }

    @Getter
    public static class LoginPayload {
        private final String clientId;
        private final String userId;
        private final String username;

        public LoginPayload(String clientId, String userId, String username) {
            this.clientId = clientId;
            this.userId = userId;
            this.username = username;
        }
    }

    @Getter
    @Builder
    public static class SendMessagePayload {
        private String id;
        private String chatRoomId;
        private Content content;
        private String senderId;
        private String senderName;
        private String senderAvatar;
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
                FILE, STRING
            }
        }
    }
}
