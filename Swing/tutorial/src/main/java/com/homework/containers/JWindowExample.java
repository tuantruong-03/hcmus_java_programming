package com.homework.containers;

import javax.swing.*;
import java.awt.*;

public class JWindowExample {
    public static void main(String[] args) {
        JWindow window = new JWindow();
        window.setSize(300, 200);
        window.setLocationRelativeTo(null); // Center the window

        JPanel panel = new JPanel();
        panel.setBackground(Color.YELLOW);
        panel.add(new JLabel("This is a JWindow!"));

        window.add(panel);
        window.setVisible(true);

        // Close window automatically after 3 seconds
        new Timer(3000, e -> window.dispose()).start();
    }
}
