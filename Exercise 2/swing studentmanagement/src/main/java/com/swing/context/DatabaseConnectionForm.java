package com.swing.context;
import com.swing.database.Database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DatabaseConnectionForm extends JFrame {
    private JTextField urlField;
    private JTextField dbNameField;
    private JTextField userField;
    private JPasswordField passwordField;
    private JButton connectButton;

    public DatabaseConnectionForm() {

        setTitle("Database Connection");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 10, 10));

        // Labels and Fields
        add(new JLabel("DB Server URL:"));
        urlField = new JTextField("jdbc:mysql://localhost:3306");
        add(urlField);

        add(new JLabel("Database Name:"));
        dbNameField = new JTextField("student_management");
        add(dbNameField);

        add(new JLabel("Username:"));
        userField = new JTextField("root");
        add(userField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        connectButton = new JButton("Connect");
        add(connectButton);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url = urlField.getText();
                String dbName = dbNameField.getText();
                String user = userField.getText();
                String password = new String(passwordField.getPassword());

                // Call method to initialize database
                initializeDatabase(url, dbName, user, password);
            }
        });

        setVisible(true);
    }

    private void initializeDatabase(String url, String dbName, String user, String password) {
        try {
            Database.ConnectionOptions opts = new Database.ConnectionOptions(url, dbName,user, password);
            Database database = new Database(opts);
            com.swing.context.ApplicationContext.init(database);
            JOptionPane.showMessageDialog(this, "Connected successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Connection failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
