package com.swing.io.message;

import lombok.*;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    private Type type;
    @Setter
    private String fileName;      // file name if type is FILE
    @Setter
    private byte[] fileData;      // file content as bytes
    private String text;
    public enum Type {
        FILE, TEXT
    }
}