package com.swing.dtos.student;

import lombok.Builder;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static com.swing.constants.ImageConstants.DEFAULT_AVATAR;

@Getter
@Builder
public class StudentResponse {
    private Long id;
    private String name;
    private Double score;
    private String image;
    private String address;
    private String note;

    public ImageIcon getImageIcon() {
        // Check if the image path is null or empty
        if (image == null || image.isEmpty()) {
            return getDefaultAvatarIcon();
        }

        // Check if the file exists at the specified path
        File imageFile = new File(image);
        if (!imageFile.exists()) {
            return getDefaultAvatarIcon(); // Return default avatar if the file does not exist
        }

        return new ImageIcon(image);
    }

    // Helper method to return the default avatar icon
    private ImageIcon getDefaultAvatarIcon() {
        return new ImageIcon(DEFAULT_AVATAR);
    }
}
