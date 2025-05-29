package com.swing.io.chatroom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetChatRoomMembersInput {
    private String chatRoomId;
}
