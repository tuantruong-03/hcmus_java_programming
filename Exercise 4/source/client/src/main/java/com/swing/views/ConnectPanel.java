package com.swing.views;

import com.swing.context.ApplicationContext;
import com.swing.context.SocketConnection;
import lombok.extern.java.Log;

import javax.swing.*;
import java.awt.*;

@Log
public class ConnectPanel  extends JPanel {
    public ConnectPanel(MainFrame parent) {
        super(new GridLayout(4, 2, 5, 5));
        setBorder(BorderFactory.createTitledBorder("Connect to Server"));

        // IP Address input
        add(new JLabel("Host: "));
        JTextField ipField = new JTextField("127.0.0.1");  // Default to localhost
        add(ipField);

        // Port input
        add(new JLabel("Port:"));
        JTextField portField = new JTextField("8080");  // Default port
        add(portField);

        // Connect button
        JButton connectButton = new JButton("Connect");
        add(connectButton);

        // Status label
        JLabel statusLabel = new JLabel("");
        add(statusLabel);

        // Action listener for the connect button
        connectButton.addActionListener(e -> {
            String host = ipField.getText();
            String portText = portField.getText();

            try {
                int port = Integer.parseInt(portText);
                if (port < 1 || port > 65535) throw new NumberFormatException("Invalid port range.");

                // Create the socket connection
                SocketConnection connection = new SocketConnection(host, port);
                Exception exception = ApplicationContext.init(connection);
                if (exception != null) {
                    log.warning(exception.getMessage());
                    statusLabel.setText("Connection failed: " + exception.getMessage());
                    statusLabel.setForeground(Color.RED);
                    return;
                }
                statusLabel.setText("Connected to " + connection.getHost() + ":" + connection.getPort());
                // Navigate to the next screen
                parent.navigateTo(MainFrame.Children.AUTH);
            } catch (NumberFormatException ex) {
                statusLabel.setText("Invalid port number!");
                statusLabel.setForeground(Color.RED);
            }
        });
    }
}
