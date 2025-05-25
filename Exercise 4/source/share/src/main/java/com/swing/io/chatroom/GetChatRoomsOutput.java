package com.swing.io.chatroom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetChatRoomsOutput {
    private List<Item> items;
    private int total;
    private int page;

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private String chatRoomId;
        private String chatRoomName;
        @JsonProperty("group")
        private boolean isGroup;
        private Date createdAt;
        private Date updatedAt;
    }
}
