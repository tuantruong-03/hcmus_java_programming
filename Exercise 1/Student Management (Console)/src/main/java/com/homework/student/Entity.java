package com.homework.student;

import java.io.Serializable;

public class Entity implements Serializable {
    private static final long serialVersionUID = 1L; // Để đảm bảo khả năng tương thích khi thay đổi class

    private String id;
    private String name;
    private double score;
    private String image;
    private String address;
    private String note;

    public Entity(String id, String name, double score, String image, String address, String note) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.image = image;
        this.address = address;
        this.note = note;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setId(String id) {
        this.id = id;
    }

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
}
