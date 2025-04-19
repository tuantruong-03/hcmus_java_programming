package com.swing.models;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RecordModel {


    private String word;
    private String meaning;

    public RecordModel() {}

    public RecordModel(String word, String meaning) {
        this.word = word;
        this.meaning = meaning;

    }
}
