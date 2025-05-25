package com.swing.io.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserListOutput {
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
