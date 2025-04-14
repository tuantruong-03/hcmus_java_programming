package com.swing.views;

import com.swing.context.ApplicationContext;
import com.swing.dtos.record.DeleteRecordRequest;
import com.swing.services.record.RecordService;

import javax.swing.*;
import java.awt.*;

public class DeleteWordDialog extends JDialog {

    private JTextField wordField;
    private JButton deleteButton;
    private JButton cancelButton;
    private boolean isDeleted = false;

    private final transient RecordService recordService;

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

        recordService = ApplicationContext.getInstance().getRecordService();
    }

    private void deleteWord() {
        String word = wordField.getText().trim().toLowerCase();
        if (word.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Từ không được để trống!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        DeleteRecordRequest request = DeleteRecordRequest.builder()
                .word(word)
                .build();
        boolean isOk = recordService.deleteOne(request);
        if (!isOk) {
            JOptionPane.showMessageDialog(this, "Đã có lỗi xảy ra, xin vui lòng thử lại!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private void cancel() {
        dispose();
    }

    public boolean isDeleted() {
        return isDeleted;
    }
}