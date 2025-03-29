package com.swing.views.student;

import com.swing.components.TableActionCellEditor;
import com.swing.components.TableActionCellRenderer;
import com.swing.components.TableImageCellRenderer;
import com.swing.context.ApplicationContext;
import com.swing.dtos.student.FilterStudentsRequest;
import com.swing.dtos.student.StudentListResponse;
import com.swing.dtos.student.StudentResponse;
import com.swing.repository.student.Student;
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
    private transient StudentService studentService;
    private List<StudentResponse> students;
    private JTable table; // Stored as an instance variable
    private DefaultTableModel tableModel;
    private String sortField = "id";
    private String sortOrder = "ASC";
    private String search;
    private Timer searchTimer; // Timer for de

    public StudentListPanel(MainFrame parent) {
        studentService = ApplicationContext.getInstance().getStudentService();
        setLayout(new BorderLayout());
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        JTable table = createTable();
        JScrollPane jScrollPane = new JScrollPane(table);
        add(jScrollPane, BorderLayout.CENTER);
        loadData();
        // Add action buttons
        JPanel buttonPanel = new JPanel();
        JButton btnBack = new JButton("Back");
        JButton btnImport = new JButton("Import CSV");
        JButton btnExport = new JButton("Export CSV");

        btnBack.addActionListener(e -> parent.navigateToDatabaseConnectionPanel());
        btnExport.addActionListener(e -> {
            List<Long> studentIds = students.stream().map(StudentResponse::getId).toList();
            new ExportStudentsToCSVDialog(this, studentIds);
        });
        btnImport.addActionListener(e -> new ImportStudentsFromCSVDialog(this));
        buttonPanel.add(btnImport);
        buttonPanel.add(btnExport);
        buttonPanel.add(btnBack);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JTable createTable() {
        Object[] columns = {StudentTable.Column.ID.getName()
                , StudentTable.Column.IMAGE.getName()
                , StudentTable.Column.NAME.getName()
                , StudentTable.Column.SCORE.getName()
                , StudentTable.Column.ADDRESS.getName()
                , StudentTable.Column.NOTE.getName()
                , StudentTable.Column.ACTION.getName()};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(50);
        table.getColumn(StudentTable.Column.IMAGE.getName()).setCellRenderer(new TableImageCellRenderer());

        table.getColumn(StudentTable.Column.ACTION.getName()).setCellRenderer(new TableActionCellRenderer());
        table.getColumn(StudentTable.Column.ACTION.getName()).setCellEditor(new TableActionCellEditor());


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
        return table;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search: "));
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
                    loadData();
                });
                searchTimer.setRepeats(false); // Make sure it runs only once after the delay
                searchTimer.start();
            }
        });
        searchPanel.add(searchField);
        panel.add(searchPanel, BorderLayout.WEST);
        JButton addButton = new JButton("+");
        addButton.addActionListener(e -> new CreateStudentDialog(this).setVisible(true));
        panel.add(addButton, BorderLayout.EAST);
        return panel;
    }

    public void loadData() {
        try {
            if (table != null && table.isEditing()) {
                table.getCellEditor().cancelCellEditing();
            }
            FilterStudentsRequest request = FilterStudentsRequest.customBuilder().search(search).sortField(sortField).sortOrder(sortOrder).build();
            StudentListResponse studentListResponse = studentService.findMany(request);
            students = studentListResponse.getStudentResponses();
            // Clear the current table data before adding new data
            tableModel.setRowCount(0); // Reset the table

            for (StudentResponse response : students) {
                JButton updateButton = new JButton("🖋️");
                JButton deleteButton = new JButton("🗑️");
                updateButton.addActionListener(e -> new UpdateStudentDialog(this, response.getId()));
                deleteButton.addActionListener(e -> new DeleteStudentDialog(this, response.getId()));
                JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                actionPanel.setBackground(Color.WHITE);
                actionPanel.add(updateButton);
                actionPanel.add(deleteButton);
                Object[] rowData = {
                        response.getId(),
                        response.getImageIcon(), // If you have a URL or image path, it can be handled separately
                        response.getName(),
                        response.getScore(),
                        response.getAddress(),
                        response.getNote(),
                        actionPanel
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
        loadData();
    }

    public void refresh() {
        studentService = ApplicationContext.getInstance().getStudentService();
        loadData();
        revalidate();
        repaint();
    }
}
