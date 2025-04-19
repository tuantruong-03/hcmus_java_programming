package com.swing.services.wordlookup;

import com.swing.dtos.wordlookup.CreateWordLookupRequest;
import com.swing.dtos.wordlookup.WordLookupsRequest;
import com.swing.exceptions.InvalidInputsException;
import com.swing.models.WordLookup;
import com.swing.repository.wordlookup.WordLookupQuery;
import com.swing.repository.wordlookup.WordLookupRepository;

import java.util.*;

public class WordLookupServiceImpl implements WordLookupService {
    private final WordLookupRepository wordLookupRepository;
    public WordLookupServiceImpl(WordLookupRepository  wordLookupRepository) {
        this.wordLookupRepository = wordLookupRepository;
    }
    public boolean createOne(CreateWordLookupRequest request) {
        InvalidInputsException exception = request.validate();
        if (exception != null) {
            throw exception;
        }
        WordLookup wordLookup = WordLookup.builder()
                .word(request.getWord())
                .language(request.getLanguage())
                .timestamp(request.getTimestamp())
                .build();
        return wordLookupRepository.createOne(wordLookup);
    }

    public List<WordLookup> findMany(WordLookupsRequest request) throws Exception {
        InvalidInputsException exception = request.validate();
        if (exception != null) {
            throw exception;
        }
        WordLookupQuery query = WordLookupQuery.builder()
                .fromTimeInMilSec(request.getFromTime())
                .toTimeInMilSec(request.getToTime())
                .build();
        return wordLookupRepository.findMany(query);
    }

    @Override
    public Map<String, Integer> countByEachWord(WordLookupsRequest request) {
        InvalidInputsException exception = request.validate();
        if (exception != null) {
            throw exception;
        }
        try {
            WordLookupQuery query = WordLookupQuery.builder()
                    .fromTimeInMilSec(request.getFromTime())
                    .toTimeInMilSec(request.getToTime())
                    .language(request.getLanguage())
                    .build();
            List<WordLookup> wordLookups = wordLookupRepository.findMany(query);
            Map<String, Integer> wordAndCount = new HashMap<>();
            for (WordLookup wordLookup : wordLookups) {
                wordAndCount.merge(wordLookup.getWord(), 1, Integer::sum);
            }
            List<Map.Entry<String, Integer>> wordAndCountEntries = new ArrayList<>(wordAndCount.entrySet().stream().toList());
            wordAndCountEntries.sort(Map.Entry.comparingByValue());
            Map<String, Integer> sortedWordAndCount = new HashMap<>();
            for (Map.Entry<String, Integer> entry : wordAndCountEntries) {
                sortedWordAndCount.put(entry.getKey(), entry.getValue());
            }
            return sortedWordAndCount;
        } catch (Exception e) {
            return Collections.emptyMap();
        }

    }

}
