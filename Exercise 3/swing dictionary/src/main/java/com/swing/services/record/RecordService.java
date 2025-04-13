package com.swing.services.record;

import com.swing.dtos.dictionary.CreateRecordRequest;
import com.swing.dtos.dictionary.DeleteRecordRequest;
import com.swing.dtos.dictionary.RecordRequest;
import com.swing.models.RecordModel;

public interface RecordService {
    RecordModel findOne(RecordRequest request);
    boolean createOne(CreateRecordRequest request);
    boolean deleteOne(DeleteRecordRequest request);
}
