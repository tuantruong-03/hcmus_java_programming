package com.swing.views;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MainPanel extends JFrame {

    private JComboBox<String> languageSelector;
    private JTextField searchField;
    private JTextArea resultArea;
    private JButton searchButton, addButton, deleteButton, favoritesButton, statsButton;

    private Map<String, List<String>> viEnDict = new HashMap<>();
    private Map<String, List<String>> enViDict = new HashMap<>();
    private boolean isViToEn = true;

    public MainPanel() {
        setTitle("Việt - Anh / Anh - Việt Dictionary");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initDummyData(); // Replace this with XML loading later
        initUI();
    }

    private void initUI() {
        // Top panel - Language selector
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        languageSelector = new JComboBox<>(new String[]{"Việt → Anh", "Anh → Việt"});
        languageSelector.addActionListener(e -> {
            isViToEn = languageSelector.getSelectedIndex() == 0;
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

        addButton.addActionListener(e -> showAddWordDialog());
        deleteButton.addActionListener(e -> showDeleteWordDialog());
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

        Map<String, List<String>> dict = isViToEn ? viEnDict : enViDict;
        List<String> meanings = dict.get(word);

        if (meanings == null) {
            resultArea.setText("Không tìm thấy từ: " + word);
        } else {
            resultArea.setText(String.join("\n", meanings));
        }
    }

    public void addWordToDict(String word, List<String> meanings) {
        Map<String, List<String>> dict = isViToEn ? viEnDict : enViDict;
        dict.put(word, meanings);
        JOptionPane.showMessageDialog(this, "Thêm thành công!");
    }

    public boolean deleteWordFromDict(String word) {
        Map<String, List<String>> dict = isViToEn ? viEnDict : enViDict;
        return dict.remove(word.trim().toLowerCase()) != null;
    }

    private void showAddWordDialog() {
        AddWordDialog dialog = new AddWordDialog(this);
        dialog.setVisible(true);
    }

    private void showDeleteWordDialog() {
        DeleteWordDialog dialog = new DeleteWordDialog(this);
        dialog.setVisible(true);
    }

    private void initDummyData() {
        viEnDict.put("xin chào", Arrays.asList("hello", "hi"));
        viEnDict.put("tạm biệt", Arrays.asList("goodbye", "bye"));

        enViDict.put("hello", Arrays.asList("xin chào", "chào bạn"));
        enViDict.put("goodbye", Arrays.asList("tạm biệt", "hẹn gặp lại"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new com.swing.test.DictionaryApp().setVisible(true));
    }
}