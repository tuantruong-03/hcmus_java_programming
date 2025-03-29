package com.swing.views.student;

import com.swing.context.ApplicationContext;
import com.swing.services.student.StudentService;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;

public class ImportStudentsFromCSVDialog extends JDialog {
    private final transient StudentService studentService;
    private final StudentListPanel parent; // Function to refresh the student list

    public ImportStudentsFromCSVDialog(StudentListPanel parent) {
        setTitle("Import Students from CSV");
        this.parent = parent;
        this.studentService = ApplicationContext.getInstance().getStudentService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Select a CSV file to import:");
        JButton browseButton = new JButton("Browse");
        JButton importButton = new JButton("Import");
        JButton cancelButton = new JButton("Cancel");

        JTextField filePathField = new JTextField(20);
        filePathField.setEditable(false);

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(label);
        panel.add(filePathField);
        panel.add(browseButton);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(importButton);
        buttonPanel.add(cancelButton);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
            }
        });

        importButton.addActionListener(e -> {
            String filePath = filePathField.getText();
            if (!filePath.isEmpty()) {
                File file = new File(filePath);
                try {
                    if (studentService.importFromCSV(file)) {
                        dispose();
                        parent.refresh();
                        JOptionPane.showMessageDialog(this, "Import successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Import failed!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch ( SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a file!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dispose());

        setSize(400, 150);
        setLocationRelativeTo(null);
    }
}
