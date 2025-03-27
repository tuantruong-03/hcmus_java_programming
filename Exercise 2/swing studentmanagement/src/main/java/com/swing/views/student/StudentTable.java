package com.swing.views.student;

public class StudentTable {

    public enum Column {
        ID(0, "ID ▲▼"),
        IMAGE(1, "Image"),
        NAME(2, "Name"),
        SCORE(3, "Score ▲▼"),
        ADDRESS(4, "Address"),
        NOTE(5, "Note"),
        ACTION(6, "Action");

        private final int index;
        private final String name;

        private Column(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }
    }
}
