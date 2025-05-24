package com.swing.io.chatroom;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetChatRoomsInput {
    private int limit;
    private int page;
}
