package com.swing.views;

import javax.swing.*;
import java.awt.*;

public class DeleteWordDialog extends JDialog {

    private JTextField wordField;
    private JButton deleteButton;
    private JButton cancelButton;
    private boolean isDeleted = false;

    public DeleteWordDialog(Frame parent) {
        super(parent, "Xóa từ", true);
        setSize(300, 150);
        setLocationRelativeTo(parent);

        wordField = new JTextField(20);

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JLabel("Nhập từ cần xóa:"));
        panel.add(wordField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        deleteButton = new JButton("Xóa");
        cancelButton = new JButton("Hủy");

        deleteButton.addActionListener(e -> deleteWord());
        cancelButton.addActionListener(e -> cancel());

        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void deleteWord() {
        String word = wordField.getText().trim().toLowerCase();

//        DictionaryApp dictApp = (DictionaryApp) getOwner();
//        if (dictApp.deleteWordFromDict(word)) {
//            isDeleted = true;
//            JOptionPane.showMessageDialog(this, "Xóa thành công.");
//        } else {
//            JOptionPane.showMessageDialog(this, "Không tìm thấy từ để xóa.");
//        }
        dispose();
    }

    private void cancel() {
        dispose();
    }

    public boolean isDeleted() {
        return isDeleted;
    }
}