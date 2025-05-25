package com.swing.io.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMessagesOutput {
    private List<Item> items;
    private int total;
    private int page;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private String messageId;
        private String chatRoomId;
        private Content content;
        private String senderId;
        private Date createdAt;
        private Date updatedAt;
    }
}
