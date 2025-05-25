package com.swing.io.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMessageInput {
    private String chatRoomId;
    private String messageId;
    private Content content;
}
