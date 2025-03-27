package com.swing.repository.student;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class Student implements Serializable {
    private static final long serialVersionUID = 1L; // Để đảm bảo khả năng tương thích khi thay đổi class
    public static final String ColumnId = "id";
    public static final String ColumnName = "name";
    public static final String ColumnScore = "score";
    public static final String ColumnImage = "image";
    public static final String ColumnAddress = "address";
    public static final String ColumnNote = "note";

    private Long id;
    private String name;
    private Double score;
    private String image;
    private String address;
    private String note;

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", score=" + score +
                ", image='" + image + '\'' +
                ", address='" + address + '\'' +
                ", note='" + note + '\'' +
                '}';
    }

    @Builder
    @Getter
    public static class UpdatePayload {
        private String name;
        private Double score;
        private String image;
        private String address;
        private String note;
    }
}
