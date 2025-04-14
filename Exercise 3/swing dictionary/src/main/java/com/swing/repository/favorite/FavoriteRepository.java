package com.swing.repository.favorite;

import com.swing.models.Favorite;

import javax.xml.stream.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public interface FavoriteRepository {
    List<Favorite> findMany(FavoriteQuery query) throws Exception;

    boolean createOne(Favorite favorite);

}
