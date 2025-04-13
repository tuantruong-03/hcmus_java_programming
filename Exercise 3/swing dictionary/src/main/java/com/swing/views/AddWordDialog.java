package com.swing.views;

import com.swing.context.ApplicationContext;
import com.swing.dtos.dictionary.CreateRecordRequest;
import com.swing.services.record.RecordService;

import javax.swing.*;
import java.awt.*;

public class AddWordDialog extends JDialog {
    private JTextField wordField;
    private JTextArea meaningsField;
    private JButton addButton;
    private JButton cancelButton;

    private final RecordService recordService;

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
        recordService = ApplicationContext.getInstance().getRecordService();
    }

    private void addWord() {
        String word = wordField.getText().trim().toLowerCase();
        String meaning = meaningsField.getText().trim();
        if (word.trim().isEmpty() || meaning.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Từ mới và nghĩa không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        CreateRecordRequest request = CreateRecordRequest.builder()
                .word(word)
                .meaning(meaning)
                .build();
        boolean isOk = recordService.createOne(request);
        if (!isOk) {
            JOptionPane.showMessageDialog(this, "Đã có lỗi xảy ra, xin vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Thêm từ mới thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private void cancel() {
        dispose();
    }

}