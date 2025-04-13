package com.swing.services.record;

import com.swing.dtos.dictionary.RecordRequest;
import com.swing.models.RecordModel;

public interface RecordService {
    public RecordModel findOne(RecordRequest request);
}
