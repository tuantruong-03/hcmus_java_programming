package com.swing.dtos.user;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class UserListResponse {
    List<Item> items;
    int total;
    int page;

    @Builder
    @Getter
    public static class Item {
        private String name;
        private String username;
    }
}
