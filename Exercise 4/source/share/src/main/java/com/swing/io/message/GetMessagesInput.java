package com.swing.io.message;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetMessagesInput {
    private String chatRoomId;
    private int page;
    private int limit;
}
