package com.swing.models;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
public class User {
    private String id;
    private String name;
    private String username;
    private String password;
    private Date createdAt;
    private Date updatedAt;
}
