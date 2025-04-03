package com.swing.views;
import com.swing.config.Database;
import com.swing.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;

public class DatabaseConnectionPanel extends JPanel {
    private final JTextField urlField;
    private final JTextField dbNameField;
    private final JTextField userField;
    private final JPasswordField passwordField;
    private final MainFrame parentFrame;

    public DatabaseConnectionPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout(10, 10)); // BorderLayout for better positioning
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // Padding

        // Panel for input fields
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        // TODO: erase default value
        inputPanel.add(new JLabel("DB Server URL:"));
        urlField = new JTextField();
        inputPanel.add(urlField);

        inputPanel.add(new JLabel("Database Name:"));
        dbNameField = new JTextField();
        inputPanel.add(dbNameField);

        inputPanel.add(new JLabel("Username:"));
        userField = new JTextField();
        inputPanel.add(userField);

        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        inputPanel.add(passwordField);

        // Centered Button Panel
        JPanel buttonPanel = new JPanel();
        JButton connectButton = new JButton("Connect");
        buttonPanel.add(connectButton); // Center the button

        // Add components to main panel
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button Click Listener
        connectButton.addActionListener(e -> {
            String url = urlField.getText();
            String dbName = dbNameField.getText();
            String user = userField.getText();
            String password = new String(passwordField.getPassword());

            initializeDatabase(url, dbName, user, password);
        });

        setVisible(true);
    }

    private void initializeDatabase(String url, String dbName, String user, String password) {
        try {
            Database.ConnectionOptions opts = new Database.ConnectionOptions(url, dbName,user, password);
            Database database = new Database(opts);
            ApplicationContext.init(database);
            JOptionPane.showMessageDialog(this, "Connected successfully!");
            parentFrame.navigateToStudentListPanel();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Connection failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
