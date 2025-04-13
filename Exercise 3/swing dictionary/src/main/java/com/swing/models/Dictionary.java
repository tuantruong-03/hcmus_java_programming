package com.swing.models;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;

import java.util.List;

@Getter
@XmlRootElement(name = "dictionary")
@XmlAccessorType(XmlAccessType.FIELD)
public class Dictionary {
    @XmlElement(name = "record")
    private List<RecordModel> records;

    public void setRecords(List<RecordModel> records) {
        this.records = records;
    }
}