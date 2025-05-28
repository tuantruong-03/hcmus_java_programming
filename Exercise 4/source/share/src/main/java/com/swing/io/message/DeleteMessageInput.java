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
public class DeleteMessageInput {
    private String chatRoomId;
    private String messageId;
    private String senderId;
    private List<String> receiverIds;
}
