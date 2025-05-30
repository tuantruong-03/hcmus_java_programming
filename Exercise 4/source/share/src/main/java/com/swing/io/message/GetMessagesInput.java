package com.swing.io.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMessagesInput {
    private String chatRoomId;
    private int page;
    private int limit;
}
