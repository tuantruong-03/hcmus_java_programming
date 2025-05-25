package com.swing.io.chatroom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetChatRoomInput {
    private String chatRoomId;
    @JsonProperty("group")
    private boolean isGroup;
}
