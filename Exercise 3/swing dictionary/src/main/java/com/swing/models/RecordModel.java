package com.swing.models;


//public record RecordModel(String word, String meaning) {
//    public static final String WORD_TAG = "word";
//    public static final String MEANING_TAG = "meaning";
//
//    @Override
//    public String toString() {
//        return "Word: " + word + "\nMeaning:\n" + meaning;
//    }
//}
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;


@Getter
@XmlRootElement(name = "record")
@XmlAccessorType(XmlAccessType.FIELD)
public class RecordModel {

    @XmlElement(name = "word")
    private String word;

    @XmlElement(name = "meaning")
    private String meaning;

    public void setWord(String word) {
        this.word = word;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}
