package com.swing.views;

import com.swing.views.auth.AuthPanel;
import com.swing.views.chat.MainChatPanel;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame  {
    private final CardLayout cardLayout = new CardLayout();
    private AuthPanel authPanel;
    private MainChatPanel chatPanel;

    public MainFrame() {
        super("Chat Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(cardLayout);

        JPanel connectPanel = new ConnectPanel(this);

        add(connectPanel, Children.CONNECT.getName());
    }
    public void navigateTo(Children panel) {
        switch (panel) {
            case AUTH:
                if (authPanel == null) {
                    authPanel = new AuthPanel(this);
                    add(authPanel, Children.AUTH.getName());
                }
                break;
            case MAIN_CHAT:
                if (chatPanel == null) {
                    chatPanel = new MainChatPanel(this);
                    add(chatPanel, Children.MAIN_CHAT.getName());
                }
                break;
        }
        cardLayout.show(getContentPane(), panel.getName());
    }


    @Getter
    public enum Children {
        AUTH("AuthPanel"),
        CONNECT("ConnectPanel"),
        MAIN_CHAT("MainChatPanel");
        private final String name;

        Children(String name) {
            this.name = name;
        }
    }

}
