package com.swing.test;

import javax.swing.*;
import java.awt.*;

public class StudentManagementUI {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    public StudentManagementUI() {
        JFrame frame = new JFrame("Quản Lý Học Sinh");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createHomePanel(), "Home");
        mainPanel.add(createStudentFormPanel(), "StudentForm");
        mainPanel.add(createStudentListPanel(), "StudentList");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Quản Lý Học Sinh", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Thêm Học Sinh");
        JButton btnList = new JButton("Xem Danh Sách");

        btnAdd.addActionListener(e -> cardLayout.show(mainPanel, "StudentForm"));
        btnList.addActionListener(e -> cardLayout.show(mainPanel, "StudentList"));

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnList);
        panel.add(buttonPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStudentFormPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Mã HS:"));
        JTextField txtId = new JTextField();
        panel.add(txtId);

        panel.add(new JLabel("Tên HS:"));
        JTextField txtName = new JTextField();
        panel.add(txtName);

        panel.add(new JLabel("Điểm:"));
        JTextField txtScore = new JTextField();
        panel.add(txtScore);

        panel.add(new JLabel("Hình ảnh:"));
        JButton btnUpload = new JButton("Chọn ảnh");
        panel.add(btnUpload);

        panel.add(new JLabel("Địa chỉ:"));
        JTextField txtAddress = new JTextField();
        panel.add(txtAddress);

        panel.add(new JLabel("Ghi chú:"));
        JTextField txtNote = new JTextField();
        panel.add(txtNote);

        JButton btnSave = new JButton("Lưu");
        JButton btnBack = new JButton("Quay lại");

        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
        panel.add(btnSave);
        panel.add(btnBack);

        return panel;
    }

    private JPanel createStudentListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"MHS", "Tên HS", "Điểm", "Địa chỉ"};
        Object[][] data = {};
        JTable table = new JTable(data, columns);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnBack = new JButton("Quay lại");
        JButton btnImport = new JButton("Import CSV");
        JButton btnExport = new JButton("Export CSV");

        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
        buttonPanel.add(btnImport);
        buttonPanel.add(btnExport);
        buttonPanel.add(btnBack);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentManagementUI::new);
    }
}
