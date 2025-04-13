package com.swing.views;

import com.swing.context.ApplicationContext;
import com.swing.context.DictionaryType;
import com.swing.dtos.dictionary.RecordRequest;
import com.swing.models.RecordModel;
import com.swing.services.record.RecordService;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {
    private JComboBox<String> languageSelector;
    private JTextField searchField;
    private JTextArea resultArea;
    private JButton searchButton, addButton, deleteButton, favoritesButton, statsButton;

    private RecordService recordService;

    public MainFrame(String title) {
        super(title);
        setTitle("Việt - Anh / Anh - Việt Dictionary");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        recordService = ApplicationContext.getInstance().getRecordService();
        initUI();
        setVisible(true);
    }

    private void initUI() {
        // Top panel - Language selector
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final String VI_EN = "Việt → Anh";
        final String EN_VI = "Anh → Việt";
        languageSelector = new JComboBox<>(new String[]{VI_EN, EN_VI});
        languageSelector.addActionListener(e -> {
            if (languageSelector.getSelectedIndex() == 0) {
                ApplicationContext.setDictionaryType(DictionaryType.VI_EN);
            } else {
                ApplicationContext.setDictionaryType(DictionaryType.EN_VI);
            }
            recordService = ApplicationContext.getInstance().getRecordService();
            resultArea.setText("");
            searchField.setText("");
        });
        topPanel.add(new JLabel("Chọn ngôn ngữ:"));
        topPanel.add(languageSelector);

        // Center panel - Search
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        searchButton = new JButton("Tra cứu");
        searchButton.addActionListener(e -> searchWord());
        searchPanel.add(new JLabel("Nhập từ:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        resultArea = new JTextArea(8, 40);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel - Add/Delete
        JPanel bottomPanel = new JPanel(new FlowLayout());
        addButton = new JButton("Thêm từ mới");
        deleteButton = new JButton("Xóa từ");
        favoritesButton = new JButton("Danh sách yêu thích");
        statsButton = new JButton("Thống kê tra cứu");

        addButton.addActionListener(e -> new AddWordDialog(this).setVisible(true));
        deleteButton.addActionListener(e -> new DeleteWordDialog(this).setVisible(true));
        favoritesButton.addActionListener(e -> showFavoritesPanel());
//        statsButton.addActionListener(e -> showStatsPanel());

        bottomPanel.add(addButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(favoritesButton);
        bottomPanel.add(statsButton);

        // Main layout
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void showFavoritesPanel() {
        JFrame favFrame = new JFrame("Từ Yêu Thích");
        favFrame.setSize(400, 300);
        favFrame.setLocationRelativeTo(this);

        FavoritesPanel favPanel = new FavoritesPanel();
        favFrame.add(favPanel);
        favFrame.setVisible(true);
    }
    private void searchWord() {
        String word = searchField.getText().trim().toLowerCase();
        if (word.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ cần tra.");
            return;
        }
        RecordModel record = recordService.findOne(RecordRequest.builder().word(word).build());

        if (record == null) {
            resultArea.setText("Không tìm thấy từ: " + word);
        } else {
            resultArea.setText(record.getMeaning());
        }
    }
}