package com.swing.views;

import com.swing.views.student.StudentListPanel;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MainFrame extends JFrame {
    private static final String DATABASE_CONNECTION_PANEL = "DatabaseConnectionPanel";
    private static final String STUDENT_LIST_PANEL = "StudentListPanel";
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private final DatabaseConnectionPanel databaseConnectionPanel;
    private StudentListPanel studentListPanel;

    public MainFrame(String title) {
        super(title);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        databaseConnectionPanel = new DatabaseConnectionPanel(this);
        mainPanel.add(databaseConnectionPanel, DATABASE_CONNECTION_PANEL);
        add(mainPanel);
        setVisible(true);
    }

    public void navigateToDatabaseConnectionPanel() {
        cardLayout.show(mainPanel, DATABASE_CONNECTION_PANEL);
    }
    public void navigateToStudentListPanel() throws SQLException {
        if (studentListPanel == null) {
            studentListPanel = new StudentListPanel(this);
            mainPanel.add(studentListPanel, STUDENT_LIST_PANEL);
            cardLayout.show(mainPanel, STUDENT_LIST_PANEL);
            return;
        }
        studentListPanel.refresh();
        cardLayout.show(mainPanel, STUDENT_LIST_PANEL);
    }
}
