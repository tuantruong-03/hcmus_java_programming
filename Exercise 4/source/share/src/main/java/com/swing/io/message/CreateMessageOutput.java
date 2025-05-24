package com.swing.io.message;

import lombok.Builder;
import lombok.Getter;
import java.util.Date;

@Builder
@Getter
public class CreateMessageOutput {
    private String chatRoomId;
    private Date createdAt;
    private Date updatedAt;
}
