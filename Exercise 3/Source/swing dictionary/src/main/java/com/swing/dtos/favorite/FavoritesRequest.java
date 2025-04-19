package com.swing.dtos.favorite;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FavoritesRequest {
    private String language;
    private String sortField;
    private String sortDirection;
}
