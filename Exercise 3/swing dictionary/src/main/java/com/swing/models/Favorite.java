package com.swing.models;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Favorite {
    public static final String English = "English";
    public static final String Vietnamese = "Vietnamese";

    private String word;
    private String language;
    private String meaning;

    public Favorite(String word,  String language, String meaning) {
        this.word = word;
        this.language = language;
        this.meaning = meaning;
    }

    public Favorite() {}
}
