package com.swing.services.favorite;

import com.swing.dtos.favorite.CreateFavoriteRequest;
import com.swing.dtos.favorite.FavoritesRequest;
import com.swing.exceptions.InvalidInputsException;
import com.swing.models.Favorite;
import com.swing.repository.favorite.FavoriteQuery;
import com.swing.repository.favorite.FavoriteRepository;
import com.swing.repository.pagination.Sort;

import java.util.List;

public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favoriteRepository;
    public FavoriteServiceImpl(FavoriteRepository  favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }
    public boolean createOne(CreateFavoriteRequest request) {
        InvalidInputsException exception = request.validate();
        if (exception != null) {
            throw exception;
        }
        Favorite favorite = new Favorite(request.getWord(), request.getLanguage(), request.getMeaning());
        return favoriteRepository.createOne(favorite);
    }

    public List<Favorite> findMany(FavoritesRequest request) throws Exception {
        boolean isAscending = true;
        if (request.getSortDirection() == null || request.getSortDirection().equalsIgnoreCase("false")) {
            isAscending = false;
        }
        Sort sort = Sort.builder().field(request.getSortField()).ascending(isAscending).build();
        FavoriteQuery query = FavoriteQuery.builder()
                .language(request.getLanguage())
                .sort(sort)
                .build();
        return favoriteRepository.findMany(query);
    }
}
