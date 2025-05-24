package com.swing.io.chatroom;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Builder
@Getter
public class GetChatRoomsOutput {
    private List<Item> items;
    private int total;
    private int page;

    @Builder
    @Getter
    public static class Item {
        private String id;
        private String name;
        private boolean isGroup;
        private Date createdAt;
        private Date updatedAt;
    }
}
