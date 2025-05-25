package com.swing.io.chatroom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetChatRoomOutput {
    private String id;
    private String name;
    private Map<String, String> members; // userId -> nickname
    @JsonProperty("group")
    private boolean isGroup;
    private Date createdAt;
    private Date updatedAt;
}
