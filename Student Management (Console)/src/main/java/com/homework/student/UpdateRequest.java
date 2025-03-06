package com.homework.student;

public class UpdateRequest {
    private final String name;
    private final Double score;
    private final String image;
    private final String address;
    private final String note;

    private UpdateRequest(Builder builder) {
        this.name = builder.name;
        this.score = builder.score;
        this.image = builder.image;
        this.address = builder.address;
        this.note = builder.note;
    }

    public String getName() {
        return name;
    }

    public Double getScore() {
        return score;
    }

    public String getImage() {
        return image;
    }

    public String getAddress() {
        return address;
    }

    public String getNote() {
        return note;
    }

    @Override
    public String toString() {
        return "UpdateStudentRequest{" +
                ", name='" + name + '\'' +
                ", score=" + score +
                ", image='" + image + '\'' +
                ", address='" + address + '\'' +
                ", note='" + note + '\'' +
                '}';
    }

    public static class Builder {
        private String name;
        private Double score;
        private String image;
        private String address;
        private String note;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder score(Double score) {
            this.score = score;
            return this;
        }

        public Builder image(String image) {
            this.image = image;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder note(String note) {
            this.note = note;
            return this;
        }

        public UpdateRequest build() {
            return new UpdateRequest(this);
        }
    }
}
