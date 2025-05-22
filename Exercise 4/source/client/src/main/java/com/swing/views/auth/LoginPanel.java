package com.swing.views.auth;

import com.swing.callers.AuthCaller;
import com.swing.context.ApplicationContext;
import com.swing.io.Output;
import com.swing.io.user.LoginUserInput;
import com.swing.io.user.LoginUserOutput;
import com.swing.types.Result;
import lombok.extern.java.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@Log
class LoginPanel extends JPanel {
    private final AuthPanel _parent;
    private final transient AuthCaller authCaller;

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel errorLabel;

    public LoginPanel(AuthPanel parent) {
        super(new GridLayout(3, 2, 5, 5));
        this._parent = parent;
        setBorder(BorderFactory.createTitledBorder("Login"));
        add(new JLabel("Username:"));
        usernameField = new JTextField("tuan.truon");
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField("123456");
        add(passwordField);

        JButton loginButton = new JButton("Login");

        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        add(errorLabel);
        loginButton.addActionListener(this::handleLoginButton);
        add(loginButton);

        this.authCaller = ApplicationContext.getInstance().getAuthCaller();
    }

    private void handleLoginButton(ActionEvent e) {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());
        try {
            Result<LoginUserInput> buildRequestResult = LoginUserInput.builder()
                    .username(username)
                    .password(password)
                    .build();
            if (buildRequestResult.isFailure()) {
                errorLabel.setText("Login failed: " + buildRequestResult.getException().getMessage());
                return;
            }
            errorLabel.setForeground(new Color(0, 128, 0));  // Success color (green)
            Result<Output<LoginUserOutput>> registerResult = authCaller.login(buildRequestResult.getValue());
            if (registerResult.isFailure()) {
                log.warning("failed to login: " + registerResult.getException());
                return;
            }
            Output<LoginUserOutput> output = registerResult.getValue();
            if (output.getError() != null) {
                errorLabel.setText("Login failed: " + output.getError().getMessage());
                return;
            }
            Exception exception = ApplicationContext.getInstance().runEventDispatcher();
            if (exception != null) {
                log.warning(exception.getMessage());
                errorLabel.setText("Login failed: " + exception.getMessage());
                return;
            }
            errorLabel.setText("Login successfully!");
            _parent.onLoginSuccess();

        } catch (Exception ex) {
            errorLabel.setText("Error: " + ex.getMessage());
        }
    }
}