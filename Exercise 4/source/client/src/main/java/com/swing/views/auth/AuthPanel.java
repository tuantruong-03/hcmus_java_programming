package com.swing.views.auth;

import com.swing.views.MainFrame;

import javax.swing.*;
import java.awt.*;

public class AuthPanel extends JPanel {
    private final MainFrame _parent;

    public AuthPanel(MainFrame parent) {
        super(new BorderLayout());
        this._parent = parent;
        setBorder(BorderFactory.createTitledBorder("Login / Register"));
        JPanel switchPanel = new JPanel();
        JButton switchToLogin = new JButton("Login");
        JButton switchToRegister = new JButton("Register");
        switchPanel.add(switchToLogin);
        switchPanel.add(switchToRegister);
        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);
        cardPanel.add(new LoginPanel(this), "Login");
        cardPanel.add(new RegisterPanel(this), "Register");
        cardLayout.show(cardPanel, "Login");

        switchToLogin.addActionListener(e -> cardLayout.show(cardPanel, "Login"));
        switchToRegister.addActionListener(e -> cardLayout.show(cardPanel, "Register"));

        add(switchPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
    }

    public void onLoginSuccess() {
        JOptionPane.showMessageDialog(this, "Login successfully!");
        _parent.navigateTo(MainFrame.Children.CHAT);
    }

}



