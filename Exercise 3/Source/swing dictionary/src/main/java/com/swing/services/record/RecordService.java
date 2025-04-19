package com.swing.services.record;

import com.swing.dtos.record.CreateRecordRequest;
import com.swing.dtos.record.DeleteRecordRequest;
import com.swing.dtos.record.RecordRequest;
import com.swing.models.RecordModel;

public interface RecordService {
    RecordModel findOne(RecordRequest request);
    boolean createOne(CreateRecordRequest request);
    boolean deleteOne(DeleteRecordRequest request);
}
