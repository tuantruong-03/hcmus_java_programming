package com.homework.components;

import javax.swing.*;


public class UserFormApp {
    public static void main(String[] args) {
        JFrame frame = new JFrame("User Registration");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Creating UserFormPanel and passing submit action
        UserFormPanel formPanel = new UserFormPanel();
        frame.add(formPanel);
        frame.setVisible(true);
    }
}
