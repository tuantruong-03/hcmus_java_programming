package com.swing.repository.record;

import com.swing.models.RecordModel;

public interface RecordRepository {
    public RecordModel findOne(RecordQuery query);
}
