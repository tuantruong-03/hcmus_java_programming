package com.swing.io.message;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Builder
@Getter
public class GetMessagesOutput {
    private List<Item> items;
    private int total;
    private int page;

    @Getter
    @Builder
    public static class Item {
        private String messageId;
        private String chatRoomId;
        private Content content;
        private String senderId;
        private Date createdAt;
        private Date updatedAt;
    }
}
