package com.swing.views;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame  {
    private final CardLayout cardLayout;
    private final RunServerPanel runServerPanel;

    public MainFrame() {
        super("Chat Server");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        runServerPanel = new RunServerPanel();

        add(runServerPanel, Children.CONNECT.getName());
        setVisible(true);
    }


    @Getter
    public enum Children {
        AUTH("AuthPanel"),
        CONNECT("ConnectPanel");
        private final String name;

        Children(String name) {
            this.name = name;
        }
    }

}
