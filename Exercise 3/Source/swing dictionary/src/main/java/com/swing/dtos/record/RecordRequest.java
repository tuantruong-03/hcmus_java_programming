package com.swing.dtos.record;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecordRequest {
    private String word;
}
