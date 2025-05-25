package com.swing.models;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Builder
@Setter
public class ChatRoom {
    private String id;
    private String name;
    private boolean isGroup;
    private boolean isNew;
    private List<String> userIds;
    private Date createdAt;
    private Date updatedAt;
}
