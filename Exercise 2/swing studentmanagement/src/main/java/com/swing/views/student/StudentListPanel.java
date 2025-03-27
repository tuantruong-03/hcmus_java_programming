package com.swing.views.student;

import com.swing.components.TableImageCellRenderer;
import com.swing.context.ApplicationContext;
import com.swing.dtos.student.FilterStudentsRequest;
import com.swing.dtos.student.StudentListResponse;
import com.swing.dtos.student.StudentResponse;
import com.swing.services.student.StudentService;
import com.swing.views.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class StudentListPanel extends JPanel {
    private final DefaultTableModel tableModel;
    private String sortField = "id";
    private String sortOrder = "ASC";
    private String search;
    private Timer searchTimer; // Timer for de

    public StudentListPanel(MainFrame parent) {
        setLayout(new BorderLayout());
        add(createSearchPanel(), BorderLayout.NORTH);
        // Initialize the table and model
        Object[] columns = {StudentTable.Column.ID.getName()
                ,StudentTable.Column.IMAGE.getName()
                ,StudentTable.Column.NAME.getName()
                ,StudentTable.Column.SCORE.getName()
                ,StudentTable.Column.ADDRESS.getName()
        ,StudentTable.Column.NOTE.getName()};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        table.setRowHeight(50);
        table.getColumn(StudentTable.Column.IMAGE.getName()).setCellRenderer(new TableImageCellRenderer());

        // Add mouse listener to handle column sorting
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = table.columnAtPoint(e.getPoint());
                if (column == StudentTable.Column.ID.getIndex()) { // ID column clicked
                    toggleSortOrder("id");
                } else if (column == StudentTable.Column.SCORE.getIndex()) { // Score column clicked
                    toggleSortOrder("score");
                }
            }
        });

        // Add the table to the JScrollPane and the panel
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Load the student data and update the table
        loadStudentData();

        // Add action buttons
        JPanel buttonPanel = new JPanel();
        JButton btnBack = new JButton("Back");
        JButton btnImport = new JButton("Import CSV");
        JButton btnExport = new JButton("Export CSV");

        btnBack.addActionListener(e -> parent.navigateToDatabaseConnectionPanel());
        buttonPanel.add(btnImport);
        buttonPanel.add(btnExport);
        buttonPanel.add(btnBack);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        searchField.setToolTipText("Search students by name or ID");

        // Add KeyListener to search bar for real-time search
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (searchTimer != null && searchTimer.isRunning()) {
                    searchTimer.stop();
                }
                searchTimer = new Timer(1000, event -> {
                    search = searchField.getText();
                    loadStudentData();
                });
                searchTimer.setRepeats(false); // Make sure it runs only once after the delay
                searchTimer.start();
            }
        });
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        return searchPanel;
    }

    public void loadStudentData() {
        try {
            StudentService studentService = ApplicationContext.getInstance().getStudentService();
            FilterStudentsRequest request = FilterStudentsRequest.customBuilder().search(search).sortField(sortField).sortOrder(sortOrder).build();
            StudentListResponse students = studentService.findMany(request);
            List<StudentResponse> studentResponses = students.getStudentResponses();
            // Clear the current table data before adding new data
            tableModel.setRowCount(0); // Reset the table

            for (StudentResponse response : studentResponses) {
                Object[] rowData = {
                        response.getId(),
                        response.getImageIcon(), // If you have a URL or image path, it can be handled separately
                        response.getName(),
                        response.getScore(),
                        response.getAddress(),
                        response.getNote()
                };
                tableModel.addRow(rowData); // Add the row to the table
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void toggleSortOrder(String field) {
        sortField = field;
        sortOrder = sortOrder.equals("ASC") ? "DESC" : "ASC";
        loadStudentData();
    }

    public void refresh() {
        loadStudentData();
        revalidate();
        repaint();
    }
}
