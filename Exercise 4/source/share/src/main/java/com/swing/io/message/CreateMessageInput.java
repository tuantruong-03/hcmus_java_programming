package com.swing.io.message;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Builder
@Getter
public class CreateMessageInput {
    private String chatRoomId;
    private Content content;
    private String senderId;
    private List<String> receiverIds;
    private Date createdAt;
    private Date updatedAt;


}
