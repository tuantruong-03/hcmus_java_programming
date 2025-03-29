package com.swing.views.student;

import com.swing.context.ApplicationContext;
import com.swing.services.student.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DeleteStudentDialog extends JDialog {

    private final StudentListPanel parent;
    private final transient StudentService studentService;
    private final Long studentId;

    public DeleteStudentDialog(StudentListPanel parent, long studentId){
        setTitle("Delete Student");
        setModal(true);
        this.parent = parent;
        this.studentService = ApplicationContext.getInstance().getStudentService();
        this.studentId = studentId;
        initUI();
    }
    private void initUI() {
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Confirmation message
        JLabel confirmLabel = new JLabel("Are you sure you want to delete the student with ID: " + studentId + "?");
        mainPanel.add(confirmLabel, BorderLayout.CENTER);

        // Buttons Panel (Delete and Cancel)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteButton = new JButton("Delete");
        JButton cancelButton = new JButton("Cancel");

        deleteButton.addActionListener(e -> deleteStudent());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void deleteStudent() {
        try {
            studentService.delete(studentId);
            JOptionPane.showMessageDialog(this, "Student updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            parent.refresh();
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid score format!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting student student!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
