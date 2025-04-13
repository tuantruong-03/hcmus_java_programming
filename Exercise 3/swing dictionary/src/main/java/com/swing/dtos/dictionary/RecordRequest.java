package com.swing.dtos.dictionary;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecordRequest {
    private String word;
}
