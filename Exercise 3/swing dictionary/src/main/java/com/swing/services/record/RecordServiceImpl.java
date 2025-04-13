package com.swing.services.record;

import com.swing.dtos.dictionary.RecordRequest;
import com.swing.models.RecordModel;
import com.swing.repository.record.RecordQuery;
import com.swing.repository.record.RecordRepository;

public class RecordServiceImpl implements RecordService {
    private final RecordRepository recordRepository;
    public RecordServiceImpl(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }
    public RecordModel findOne(RecordRequest request) {
        RecordQuery query = RecordQuery.builder()
                        .word(request.getWord())
                .build();
        return recordRepository.findOne(query);
    }
}
