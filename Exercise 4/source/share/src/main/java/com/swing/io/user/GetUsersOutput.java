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
public class GetUsersOutput {
    private List<Item> items;
    private  int total;
    private  int page;

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private String id;
        private String name;
        private String username;
        @Override
        public String toString() {
            return name + " (" + username + ")";
        }
    }
}
