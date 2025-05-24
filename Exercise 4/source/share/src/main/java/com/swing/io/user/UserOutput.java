package com.swing.io.user;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserOutput {
    private String id;
    private String username;
}
