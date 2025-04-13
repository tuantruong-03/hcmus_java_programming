package com.swing.repository.record;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RecordQuery {
    private String word;
    private List<String> inWords;
}
