package com.swing.views.auth;

import com.swing.callers.AuthCaller;
import com.swing.context.ApplicationContext;
import com.swing.dtos.user.RegisterUserRequest;
import com.swing.types.Result;
import lombok.extern.java.Log;

import javax.swing.*;
import java.awt.*;

@Log
class RegisterPanel extends JPanel {
    private final AuthCaller authCaller;

    private final AuthPanel parent;

    public RegisterPanel(AuthPanel parent) {
        super(new GridLayout(5, 2, 5, 5));  // Adjusted layout for the error message
        setBorder(BorderFactory.createTitledBorder("Register"));
        this.authCaller = ApplicationContext.getInstance().getAuthCaller();
        this.parent = parent;
        // Name field
        add(new JLabel("Name:"));
        JTextField nameField = new JTextField("Tuan Truong");
        add(nameField);

        // Username field
        add(new JLabel("Username:"));
        JTextField usernameField = new JTextField("tuan.truong");
        add(usernameField);

        // Password field
        add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField("123456");
        add(passwordField);

        // Register button with smaller size
        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.PLAIN, 12));  // Smaller font
        registerButton.setPreferredSize(new Dimension(80, 30));     // Smaller button size
        add(registerButton);

        // Error message label
        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        add(errorLabel);

        // Register button action listener
        registerButton.addActionListener(e -> {
            String name = nameField.getText();
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());

            try {
                // Build the registration request
                Result<RegisterUserRequest> buildRequestResult = RegisterUserRequest.builder()
                        .name(name)
                        .username(username)
                        .password(password)
                        .build();
                if (buildRequestResult.isFailure()) {
                    errorLabel.setText("Registration failed: " + buildRequestResult.getException().getMessage());
                    return;
                }
                errorLabel.setForeground(new Color(0, 128, 0));  // Success color (green)
                Result<?> registerResult = authCaller.register(buildRequestResult.getValue());
                if (registerResult.isFailure()) {
                    log.warning("failed to register: " + registerResult.getException());
                    return;
                }
                errorLabel.setText("Register successfully!");

            } catch (Exception ex) {
                errorLabel.setText("Error: " + ex.getMessage());
            }
        });

    }
}
