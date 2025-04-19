package com.swing.views;

import com.swing.context.ApplicationContext;
import com.swing.context.DictionaryType;
import com.swing.dtos.wordlookup.WordLookupsRequest;
import com.swing.services.wordlookup.WordLookupService;
import com.swing.util.DateUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class WordLookupFrequencyPanel extends JPanel {
    private final JSpinner startDateSpinner;
    private final JSpinner endDateSpinner;
    private JButton showButton;
    private JTable table;
    private final DefaultTableModel tableModel;
    private final WordLookupService wordLookupService;

    private Map<String, Integer> mockLookupData;

    public WordLookupFrequencyPanel() {
        setSize(500, 300);
        setLayout(new BorderLayout());

        // Top Panel - Date pickers and button
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        startDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startDateSpinner, "dd/MM/yyyy");
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endDateSpinner, "dd/MM/yyyy");
        startDateSpinner.setEditor(startEditor);
        endDateSpinner.setEditor(endEditor);

        showButton = new JButton("Xem thống kê");
        showButton.addActionListener(e -> showStatistics());

        topPanel.add(new JLabel("Từ:"));
        topPanel.add(startDateSpinner);
        topPanel.add(new JLabel("Đến:"));
        topPanel.add(endDateSpinner);
        topPanel.add(showButton);

        add(topPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);

        wordLookupService = ApplicationContext.getInstance().getWordLookupService();
    }

    private void showStatistics() {
        Date startDate = (Date) startDateSpinner.getValue();
        Date endDate = (Date) endDateSpinner.getValue();
        if (endDate.compareTo(startDate) < 0) {
            JOptionPane.showMessageDialog(this, "Ngày chọn không hợp lệ, vui lòng chọn lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String formattedStart = formatDate(startDate);
        String formattedEnd = formatDate(endDate);

        // Clear and set headers
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        tableModel.addColumn("Từ");
        String columnTitle = String.format(
                "<html><table style='height: 100%%; width: 100%%;'><tr><td style='vertical-align: middle;'>%s</td>" +
                        "<td style='border-left: 1px solid black; height: 100%%; margin-left: 1px;'></td>" +
                        "<td style='vertical-align: middle;'>%s</td></tr></table></html>", formattedStart, formattedEnd);
        tableModel.addColumn(columnTitle);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        String language = ApplicationContext.getDictionaryType() == DictionaryType.VI_EN
                ? "Vietnamese" : "English";
        WordLookupsRequest request = WordLookupsRequest.builder()
                .fromTime(DateUtils.getStartOfDay(startDate).getTime())
                .toTime(DateUtils.getEndOfDay(endDate).getTime())
                .language(language)
                .build();
        mockLookupData = wordLookupService.countByEachWord(request);

        // Populate mock data (replace this with real DB data)
        for (Map.Entry<String, Integer> entry : mockLookupData.entrySet()) {
            String word = entry.getKey();
            int total = entry.getValue();
            tableModel.addRow(new Object[]{word, total + " (lần)"});
        }
    }

    private String formatDate(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return String.format("%d/%d/%d", localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear());
    }

}
