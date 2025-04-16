package com.swing.services.wordlookup;

import com.swing.dtos.wordlookup.CreateWordLookupRequest;
import com.swing.dtos.wordlookup.WordLookupStats;
import com.swing.dtos.wordlookup.WordLookupsRequest;
import com.swing.models.Favorite;
import com.swing.models.WordLookup;

import java.util.List;
import java.util.Map;

public interface WordLookupService {
    boolean createOne(CreateWordLookupRequest request);
    List<WordLookup> findMany(WordLookupsRequest request) throws Exception;
    Map<String, Integer> CountByEachWord(WordLookupsRequest request) throws Exception;
}
