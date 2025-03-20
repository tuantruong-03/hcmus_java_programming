package com.homework.containers;

import javax.swing.*;


// A JFrame is the main window where you can add components like buttons, panels, and labels.
public class JFrameExample {
    public static void main(String[] args) {
        JFrame frame = new JFrame("JFrame Example hehe");
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}
