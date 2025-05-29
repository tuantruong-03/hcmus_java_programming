package com.swing.views.auth;

import com.swing.callers.AuthCaller;
import com.swing.callers.CallerUtils;
import com.swing.callers.UserCaller;
import com.swing.context.ApplicationContext;
import com.swing.context.AuthContext;
import com.swing.io.Output;
import com.swing.io.user.LoginUserInput;
import com.swing.io.user.LoginUserOutput;
import com.swing.io.user.GetUserOutput;
import com.swing.types.Result;
import lombok.extern.java.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@Log
class LoginPanel extends JPanel {
    private final AuthPanel _parent;
    private final transient UserCaller userCaller;
    private final transient AuthCaller authCaller;

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel errorLabel;

    public LoginPanel(AuthPanel parent) {
        super(new GridLayout(3, 2, 5, 5));
        this._parent = parent;
        setBorder(BorderFactory.createTitledBorder("Login"));
        add(new JLabel("Username:"));
//        usernameField = new JTextField("tuan.truon");
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
//        passwordField = new JPasswordField("123456");
        passwordField = new JPasswordField();
        add(passwordField);

        JButton loginButton = new JButton("Login");

        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        add(errorLabel);
        loginButton.addActionListener(this::handleLoginButton);
        add(loginButton);

        this.authCaller = ApplicationContext.getInstance().getAuthCaller();
        this.userCaller = ApplicationContext.getInstance().getUserCaller();
    }

    private void handleLoginButton(ActionEvent e) {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());
        Result<LoginUserInput> buildRequestResult = LoginUserInput.builder()
                .username(username)
                .password(password)
                .build();
        if (buildRequestResult.isFailure()) {
            errorLabel.setText("Login failed: " + buildRequestResult.getException().getMessage());
            return;
        }
        Result<Output<LoginUserOutput>> loginResult = authCaller.login(buildRequestResult.getValue());
        if (loginResult.isFailure()) {
            log.warning("failed to login: " + loginResult.getException());
            errorLabel.setText("Login failed, please try again" );
            return;
        }
        Output<LoginUserOutput> output = loginResult.getValue();
        if (output.getError() != null) {
            log.warning("failed to login: " + output.getError().getMessage());
            errorLabel.setText("Username or password is incorrect");
            return;
        }
        LoginUserOutput loginUserOutput = output.getBody();
        CallerUtils.INSTANCE.setToken(loginUserOutput.getToken());
        Result<Output<GetUserOutput>> result = userCaller.getMyProfile();
        if (result.isFailure()) {
            log.warning("failed to login: " + result.getException().getMessage());
            errorLabel.setText("Login failed, please try again" );
            return;
        }
        GetUserOutput getUserOutput = result.getValue().getBody();
        AuthContext.Principal principal = new AuthContext.Principal(getUserOutput.getId(), getUserOutput.getName(), getUserOutput.getUsername());
        AuthContext.INSTANCE.setPrincipal(principal);
        Exception exception = ApplicationContext.getInstance().runEventDispatcher();
        if (exception != null) {
            log.warning(exception.getMessage());
            errorLabel.setText("Login failed, please try again" + exception.getMessage());
            return;
        }
        errorLabel.setForeground(new Color(0, 128, 0));  // Success color (green)
        errorLabel.setText("Login successfully!");
        _parent.onLoginSuccess();
    }
}