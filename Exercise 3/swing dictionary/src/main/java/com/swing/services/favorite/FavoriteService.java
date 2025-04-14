package com.swing.services.favorite;

import com.swing.dtos.favorite.CreateFavoriteRequest;
import com.swing.dtos.favorite.FavoritesRequest;
import com.swing.models.Favorite;

import java.util.List;

public interface FavoriteService {
    boolean createOne(CreateFavoriteRequest request);
    List<Favorite> findMany(FavoritesRequest request) throws Exception;
}
