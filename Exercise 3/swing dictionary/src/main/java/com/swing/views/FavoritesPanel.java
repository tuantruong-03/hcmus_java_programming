package com.swing.views;

import javax.swing.*;
import java.awt.*;

public class FavoritesPanel extends JPanel {

    private JList<String> favoritesList;
    private DefaultListModel<String> listModel;

    public FavoritesPanel() {
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        favoritesList = new JList<>(listModel);
        add(new JScrollPane(favoritesList), BorderLayout.CENTER);

        // Placeholder: Add favorite words for testing
        addFavoriteWord("hello");
        addFavoriteWord("tạm biệt");
    }

    public void addFavoriteWord(String word) {
        listModel.addElement(word);
    }
}