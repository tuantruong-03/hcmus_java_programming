package com.swing.repository.record;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecordQuery {
    private String word;
}
