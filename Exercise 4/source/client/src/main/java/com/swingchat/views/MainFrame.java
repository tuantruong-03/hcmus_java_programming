package com.swingchat.views;

import com.swingchat.views.auth.AuthPanel;
import com.swingchat.views.chat.MainChatPanel;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame  {
    private final CardLayout cardLayout = new CardLayout();
    public MainFrame() {
        super("Chat Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(cardLayout);

        JPanel connectPanel = new ConnectPanel(this);
        JPanel authPanel = new AuthPanel(this);
        JPanel mainChatPanel = new MainChatPanel(this);

        add(connectPanel, Children.CONNECT.getName());
        add(authPanel, Children.AUTH.getName());
        add(mainChatPanel, Children.CHAT.getName());

        setVisible(true);
    }
    public void navigateTo(Children panel) {
        cardLayout.show(getContentPane(), panel.getName());
    }

    @Getter
    public enum Children {
        AUTH("AuthPanel"),
        CONNECT("ConnectPanel"),
        CHAT("ChatPanel");
        private final String name;

        Children(String name) {
            this.name = name;
        }
    }

}
