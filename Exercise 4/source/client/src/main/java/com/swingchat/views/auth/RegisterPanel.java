package com.swingchat.views.auth;

import javax.swing.*;
import java.awt.*;

class RegisterPanel extends JPanel {
    public RegisterPanel(AuthPanel parent) {
        super(new GridLayout(4, 2, 5, 5));
        setBorder(BorderFactory.createTitledBorder("Register"));

        add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        add(nameField);

        add(new JLabel("Username:"));
        JTextField usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField();
        add(passwordField);

        JButton registerButton = new JButton("Register");
        add(registerButton);
    }
}
