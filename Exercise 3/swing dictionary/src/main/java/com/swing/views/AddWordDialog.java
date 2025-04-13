package com.swing.views;

import javax.swing.*;
import java.awt.*;

public class AddWordDialog extends JDialog {

    private JTextField wordField;
    private JTextArea meaningsField;
    private JButton addButton;
    private JButton cancelButton;
    private boolean isAdded = false;

    public AddWordDialog(Frame parent) {
        super(parent, "Thêm từ mới", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);

        wordField = new JTextField();
        meaningsField = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(meaningsField);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel("Từ mới:"), BorderLayout.NORTH);
        panel.add(wordField, BorderLayout.CENTER);
        panel.add(new JLabel("Nghĩa (mỗi nghĩa trên 1 dòng):"), BorderLayout.SOUTH);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.NORTH);
        wrapper.add(scrollPane, BorderLayout.CENTER);

        add(wrapper, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        addButton = new JButton("Thêm");
        cancelButton = new JButton("Hủy");

        addButton.addActionListener(e -> addWord());
        cancelButton.addActionListener(e -> cancel());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addWord() {
        String word = wordField.getText().trim().toLowerCase();
        String[] meaningsArray = meaningsField.getText().split("\\n");
        isAdded = true;
        dispose();
    }

    private void cancel() {
        dispose();
    }

    public boolean isAdded() {
        return isAdded;
    }
}