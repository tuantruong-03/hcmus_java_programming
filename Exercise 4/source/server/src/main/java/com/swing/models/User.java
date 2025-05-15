package com.swing.models;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class User {
    private String id;
    private String name;
    private String username;
    private String password;
}
