package com.swing.views.student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.swing.context.ApplicationContext;
import com.swing.dtos.student.CreateStudentRequest;
import com.swing.services.student.StudentService;

public class CreateStudentDialog extends JDialog {
    private JTextField nameField;
    private JTextField scoreField;
    private JTextField addressField;
    private JTextArea noteField;
    private JLabel imageLabel;
    private File selectedFile;

    private final transient StudentService studentService;
    private final StudentListPanel parent; // Function to refresh the student list

    public CreateStudentDialog(StudentListPanel parent) {
        setTitle("Add Student");
        setModal(true);
        this.studentService = ApplicationContext.getInstance().getStudentService();
        this.parent = parent;
        initUI();
    }

    private void initUI() {
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        nameField = new JTextField(20);
        scoreField = new JTextField(20);
        addressField = new JTextField(20);
        noteField = new JTextArea(3, 20);
        noteField.setLineWrap(true);
        noteField.setWrapStyleWord(true);

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Score:"));
        formPanel.add(scoreField);
        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Note:"));
        formPanel.add(new JScrollPane(noteField));

        // Image selection button (below form fields)
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton chooseImageButton = new JButton("Choose Image");
        imageLabel = new JLabel("No image selected");
        chooseImageButton.addActionListener(e -> selectImage());

        imagePanel.add(chooseImageButton);
        imagePanel.add(imageLabel);

        // Buttons Panel (Add and Cancel)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add");

        JButton cancelButton = new JButton("Cancel");

        addButton.addActionListener(e -> saveStudent());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        // Adding all components to the main layout
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(imagePanel, BorderLayout.NORTH); // Image section at the top of the bottom panel
        southPanel.add(buttonPanel, BorderLayout.SOUTH); // Buttons section at the bottom of the panel

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(southPanel, BorderLayout.SOUTH); // South panel for the image and buttons

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Student Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            imageLabel.setText(selectedFile.getName());
        }
    }

    private void saveStudent() {
        try {
            String name = nameField.getText().trim();
            String scoreText = scoreField.getText().trim();
            String address = addressField.getText().trim();
            String note = noteField.getText().trim();

            if (name.isEmpty() || scoreText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Score are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double score = Double.parseDouble(scoreText);
            if (score < 0 || score > 10) {
                JOptionPane.showMessageDialog(this, "Score must be between 0 and 10!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String imagePath = selectedFile != null ? saveImage(selectedFile) : null;
            CreateStudentRequest request = CreateStudentRequest.builder()
                    .name(name)
                    .score(score)
                    .address(address)
                    .note(note)
                    .image(imagePath)
                    .build();
            studentService.create(request);

            JOptionPane.showMessageDialog(this, "Student added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            parent.refresh();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid score format!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving student!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String saveImage(File file) throws IOException {
        File directory = new File("images");
        if (!directory.exists()) {
            directory.mkdir();
        }

        String newFileName = System.currentTimeMillis() + "_" + file.getName();
        File destination = new File(directory, newFileName);

        Files.copy(file.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return destination.getPath();
    }
}
