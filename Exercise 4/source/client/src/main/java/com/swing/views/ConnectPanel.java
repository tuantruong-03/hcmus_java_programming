package com.swing.views;

import javax.swing.*;
import java.awt.*;

public class ConnectPanel  extends JPanel {
    public ConnectPanel(MainFrame parent) {
        super(new GridLayout(4, 2, 5, 5));
        setBorder(BorderFactory.createTitledBorder("Connect to Server"));

        add(new JLabel("IP Address:"));
        JTextField ipField = new JTextField();
        add(ipField);

        add(new JLabel("Port:"));
        JTextField portField = new JTextField();
        add(portField);

        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(e -> parent.navigateTo(MainFrame.Children.AUTH));
        add(connectButton);
    }
}
