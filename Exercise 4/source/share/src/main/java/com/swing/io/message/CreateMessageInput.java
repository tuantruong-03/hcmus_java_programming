package com.swing.io.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMessageInput {
    private String chatRoomId;
    private Content content;
    private String senderId;
    private List<String> receiverIds;
}
