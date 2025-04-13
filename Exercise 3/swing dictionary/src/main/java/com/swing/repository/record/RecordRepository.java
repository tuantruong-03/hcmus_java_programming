package com.swing.repository.record;

import com.swing.models.RecordModel;

public interface RecordRepository {
    public RecordModel findOne(RecordQuery query);
    public boolean createOne(RecordModel recordModel);
    public boolean deleteOne(RecordQuery query);
}
