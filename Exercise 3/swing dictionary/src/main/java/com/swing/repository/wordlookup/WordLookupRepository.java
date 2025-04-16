package com.swing.repository.wordlookup;

import com.swing.models.WordLookup;

import java.util.List;

public interface WordLookupRepository {
    List<WordLookup> findMany(WordLookupQuery query) throws Exception;

    boolean createOne(WordLookup wordLookup);

}
