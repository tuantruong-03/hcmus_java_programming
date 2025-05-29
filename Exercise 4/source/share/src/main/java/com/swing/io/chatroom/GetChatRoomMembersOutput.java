package com.swing.io.chatroom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetChatRoomMembersOutput {
    private List<Item> items;
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private String userId;
        private String username;
        private String nickname;
    }
}
