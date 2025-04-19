package com.swing.views;

import com.swing.context.ApplicationContext;
import com.swing.context.DictionaryType;
import com.swing.dtos.favorite.FavoritesRequest;
import com.swing.models.Favorite;
import com.swing.services.favorite.FavoriteService;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.table.*;


@Getter
enum Column {
    WORD(0, "Từ ▲▼"),
    MEANING(1, "Nghĩa");

    private final int index;
    private final String name;

    Column(int index, String name) {
        this.index = index;
        this.name = name;
    }

}


public class FavoritesPanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel tableModel;
    private String sortField = "word";
    private String sortOrder = "ASC";
    private final transient FavoriteService favoriteService;
    public FavoritesPanel() {
        favoriteService = ApplicationContext.getInstance().getFavoriteService();
        setLayout(new BorderLayout());

        // Define table columns
        String[] columnNames = {Column.WORD.getName(), Column.MEANING.getName()};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable cell editing
            }
        };

        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);  // Turn
        table.getColumnModel().getColumn(Column.WORD.getIndex()).setPreferredWidth(150);  // Word column is narrow
        table.getColumnModel().getColumn(Column.MEANING.getIndex()).setPreferredWidth(400); // Meaning column is wider

        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = table.columnAtPoint(e.getPoint());
                if (column == Column.WORD.getIndex()) { // ID column clicked
                    toggleSortOrder("word");
                } else if (column == Column.MEANING.getIndex()) { // Score column clicked
                    toggleSortOrder("meaning");
                }
                loadData();
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
        loadData();
    }

    private void toggleSortOrder(String field) {
        sortField = field;
        sortOrder = sortOrder.equals("ASC") ? "DESC" : "ASC";
    }
    private void loadData() {
        try {
            String language = ApplicationContext.getDictionaryType() == DictionaryType.VI_EN
                    ? "Vietnamese" : "English";
            FavoritesRequest request = FavoritesRequest.builder()
                    .language(language)
                    .sortField(sortField)
                    .sortDirection(sortOrder.equals("ASC") ? "true" : "false")
                    .build();
            List<Favorite> favorites = favoriteService.findMany(request);
            tableModel.setRowCount(0); // Clear
            for (Favorite fav : favorites) {
                tableModel.addRow(new Object[]{fav.getWord(), fav.getMeaning()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách yêu thích: " + e.getMessage());
        }
    }
}
