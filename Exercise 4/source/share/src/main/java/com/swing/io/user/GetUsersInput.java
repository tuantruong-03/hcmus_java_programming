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
public class GetUsersInput {
    private List<String> inUserIds;
    private int page;
    private int limit;
}
