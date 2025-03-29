package com.swing.views.student;

import com.swing.context.ApplicationContext;
import com.swing.services.student.StudentService;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class ExportStudentsToCSVDialog extends JDialog {
    private final transient StudentService studentService;
    private final List<Long> studentIds;
    private final StudentListPanel parent;

    public ExportStudentsToCSVDialog(StudentListPanel parent, List<Long> studentIds) {
        setTitle("Export Students to CSV");
        this.parent = parent;
        this.studentService = ApplicationContext.getInstance().getStudentService();
        this.studentIds = studentIds;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Choose location to save CSV:");
        JButton browseButton = new JButton("Browse");
        JButton exportButton = new JButton("Export");
        JButton cancelButton = new JButton("Cancel");

        JTextField filePathField = new JTextField(20);
        filePathField.setEditable(false);

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(label);
        panel.add(filePathField);
        panel.add(browseButton);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(exportButton);
        buttonPanel.add(cancelButton);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save CSV File");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnVal = fileChooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (!selectedFile.getAbsolutePath().endsWith(".csv")) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".csv");
                }
                filePathField.setText(selectedFile.getAbsolutePath());
            }
        });

        exportButton.addActionListener(e -> {
            String filePath = filePathField.getText();
            if (!filePath.isEmpty()) {
                File file = new File(filePath);
                if (studentService.exportToCSV(studentIds, file)) {
                    dispose();
                    parent.refresh();
                    JOptionPane.showMessageDialog(this, "Export successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Export failed!", "Error", JOptionPane.ERROR_MESSAGE);
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