package com.swing.io.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMessageOutput {
    private String messageId;
    private String chatRoomId;
    private Date createdAt;
    private Date updatedAt;
}
