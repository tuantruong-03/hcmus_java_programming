package com.swing.views.auth;

import com.swing.callers.AuthCaller;
import com.swing.context.ApplicationContext;
import com.swing.io.Output;
import com.swing.io.user.RegisterUserInput;
import com.swing.types.Result;
import lombok.extern.java.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@Log
class RegisterPanel extends JPanel {
    private final AuthCaller authCaller;

    private final AuthPanel parent;

    JTextField nameField, usernameField;
    JPasswordField passwordField;
    JLabel errorLabel;

    public RegisterPanel(AuthPanel parent) {
        super(new GridLayout(4, 2, 5, 5));  // Adjusted layout for the error message
        setBorder(BorderFactory.createTitledBorder("Register"));
        this.authCaller = ApplicationContext.getInstance().getAuthCaller();
        this.parent = parent;
        // Name field
        add(new JLabel("Name:"));
//        nameField = new JTextField("Jane Bach");
        nameField = new JTextField();
        add(nameField);

        // Username field
        add(new JLabel("Username:"));
//        usernameField = new JTextField("jane.bach");
        usernameField = new JTextField();
        add(usernameField);

        // Password field
        add(new JLabel("Password:"));
//        passwordField = new JPasswordField("123456");
        passwordField = new JPasswordField();
        add(passwordField);

        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.PLAIN, 12));  // Smaller font
        registerButton.setPreferredSize(new Dimension(80, 30));     // Smaller button size
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        add(errorLabel);
        add(registerButton);



        // Register button action listener
        registerButton.addActionListener(this::handleRegisterButton);
    }

    private void handleRegisterButton(ActionEvent e) {
        String name = nameField.getText();
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        Result<RegisterUserInput> buildRequestResult = RegisterUserInput.builder()
                .name(name)
                .username(username)
                .password(password)
                .build();
        if (buildRequestResult.isFailure()) {
            errorLabel.setText("Registration failed: " + buildRequestResult.getException().getMessage());
            return;
        }
        errorLabel.setForeground(new Color(0, 128, 0));  // Success color (green)
        Result<Output<Void>> registerResult = authCaller.register(buildRequestResult.getValue());
        if (registerResult.isFailure()) {
            log.warning("failed to register: " + registerResult.getException());
            errorLabel.setText("Registration failed: " + buildRequestResult.getException().getMessage());
            return;
        }
        Output<Void> output = registerResult.getValue();
        if (output.getError() != null) {
            log.warning("failed to register: " + output.getError().getMessage());
            errorLabel.setText("Registration failed: " + output.getError().getMessage());
            return;
        }
        errorLabel.setText("Register successfully!");

    }
}
