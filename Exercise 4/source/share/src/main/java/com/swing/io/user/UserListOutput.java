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
    private List<Item> items;
    private  int total;
    private  int page;

    @Builder
    @Getter
    public static class Item {
        private String name;
        private String username;
    }
}
