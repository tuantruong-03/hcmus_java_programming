package com.swing;

import com.swing.views.MainFrame;

import javax.swing.*;


public class Client {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}