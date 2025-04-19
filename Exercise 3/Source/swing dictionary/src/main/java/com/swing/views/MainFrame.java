package com.swing.views;

import com.swing.context.ApplicationContext;
import com.swing.context.DictionaryType;
import com.swing.dtos.favorite.CreateFavoriteRequest;
import com.swing.dtos.record.RecordRequest;
import com.swing.dtos.wordlookup.CreateWordLookupRequest;
import com.swing.models.RecordModel;
import com.swing.services.record.RecordService;
import com.swing.services.wordlookup.WordLookupService;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class MainFrame extends JFrame {
    private JComboBox<String> languageSelector;
    private JTextField searchField;
    private JTextArea resultArea;
    private JButton searchButton, addButton, deleteButton, addToFavoriteButton, favoritesButton, statsButton;

    private transient RecordModel foundRecord;

    private transient RecordService recordService;
    private transient WordLookupService wordLookupService;

    public MainFrame(String title) {
        super(title);
        setTitle("Việt - Anh / Anh - Việt Dictionary");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        recordService = ApplicationContext.getInstance().getRecordService();
        wordLookupService = ApplicationContext.getInstance().getWordLookupService();
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
        addToFavoriteButton = new JButton("Thêm vào yêu thích");
        addToFavoriteButton.addActionListener(e -> addToFavorites());
        JPanel favoritePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        favoritePanel.add(addToFavoriteButton);
        centerPanel.add(favoritePanel, BorderLayout.SOUTH);
        // Bottom panel - Add/Delete
        JPanel bottomPanel = new JPanel(new FlowLayout());
        addButton = new JButton("Thêm từ mới");
        deleteButton = new JButton("Xóa từ");
        favoritesButton = new JButton("Danh sách yêu thích");
        statsButton = new JButton("Thống kê tra cứu");

        addButton.addActionListener(e -> new AddWordDialog(this).setVisible(true));
        deleteButton.addActionListener(e -> new DeleteWordDialog(this).setVisible(true));
        favoritesButton.addActionListener(e -> showFavoritesPanel());
        statsButton.addActionListener(e -> showWordLookupFrequencyPanel());

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

    private void addToFavorites() {
        if (foundRecord == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tra từ hợp lệ để thêm vào yêu thích");
            return;
        }
        String language = ApplicationContext.getDictionaryType() == DictionaryType.VI_EN
                ? "Vietnamese" : "English";
        CreateFavoriteRequest request = CreateFavoriteRequest.builder()
                .word(foundRecord.getWord())
                .language(language)
                .meaning(foundRecord.getMeaning())
                .build();

        boolean success = ApplicationContext.getInstance().getFavoriteService().createOne(request);

        if (success) {
            JOptionPane.showMessageDialog(this, "Đã thêm vào yêu thích!");
        } else {
            JOptionPane.showMessageDialog(this, "Không thể thêm vào yêu thích.");
        }
    }


    private void showFavoritesPanel() {
        JFrame frame = new JFrame("Từ Yêu Thích");
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(this);
        FavoritesPanel panel = new FavoritesPanel();
        frame.add(panel);
        frame.setVisible(true);
    }

    private void showWordLookupFrequencyPanel() {
        JFrame frame = new JFrame("Từ Đã Tra Cứu");
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(this);
        WordLookupFrequencyPanel panel = new WordLookupFrequencyPanel();
        frame.add(panel);
        frame.setVisible(true);
    }

    private void searchWord() {
        String word = searchField.getText().trim().toLowerCase();
        if (word.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ cần tra.");
            return;
        }
        foundRecord = recordService.findOne(RecordRequest.builder().word(word).build());

        if (foundRecord == null) {
            resultArea.setText("Không tìm thấy từ: " + word);
            return;
        }
        resultArea.setText(foundRecord.getMeaning());
        String language = ApplicationContext.getDictionaryType() == DictionaryType.VI_EN
                ? "Vietnamese" : "English";
        CreateWordLookupRequest request = CreateWordLookupRequest.builder()
                .word(foundRecord.getWord())
                .language(language)
                .timestamp(new Date().getTime())
                .build();
        if (!wordLookupService.createOne(request)) {
            JOptionPane.showMessageDialog(this, "Đã có lỗi xảy ra khi thêm từ vào lịch sử tra cứu", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}