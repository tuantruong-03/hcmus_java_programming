package com.swing.io.chatroom;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckChatRoomExistenceInput {
    private List<String> userIds;
    @JsonProperty("group")
    private boolean isGroup;
}
