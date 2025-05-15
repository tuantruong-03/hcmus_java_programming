package com.swing.views.auth;

import javax.swing.*;
import java.awt.*;

class LoginPanel extends JPanel {
    public LoginPanel(AuthPanel parent) {
        super(new GridLayout(3, 2, 5, 5));
        setBorder(BorderFactory.createTitledBorder("Login"));

        add(new JLabel("Username:"));
        JTextField usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField();
        add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            parent.onLoginSuccess();
        });
        add(loginButton);
    }
}