package com.swing.context;


public enum DictionaryType {
    EN_VI("en-vi"),
    VI_EN("vi-en");

    private final String value;

    DictionaryType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DictionaryType fromValue(String value) {
        for (DictionaryType type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown dictionary type: " + value);
    }
}
