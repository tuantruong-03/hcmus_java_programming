package com.swing.repository.favorite;

import com.swing.repository.pagination.Sort;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FavoriteQuery {
    private String language;
    private Sort sort;
}
