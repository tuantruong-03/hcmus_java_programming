package com.swing.services.record;

import com.swing.dtos.dictionary.CreateRecordRequest;
import com.swing.dtos.dictionary.DeleteRecordRequest;
import com.swing.dtos.dictionary.RecordRequest;
import com.swing.exceptions.InvalidInputsException;
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

    public boolean createOne(CreateRecordRequest request) {
        InvalidInputsException exception = request.validate();
        if (exception != null) {
            throw exception;
        }
        RecordModel recordModel = new RecordModel(request.getWord(), request.getMeaning());
        return recordRepository.createOne(recordModel);
    }
    public boolean deleteOne(DeleteRecordRequest request) {
        InvalidInputsException exception = request.validate();
        if (exception != null) {
            throw exception;
        }
        RecordQuery recordQuery = RecordQuery.builder()
                .word(request.getWord())
                .build();
        return recordRepository.deleteOne(recordQuery);
    }
}
