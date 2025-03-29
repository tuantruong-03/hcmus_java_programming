package com.swing;

import com.swing.views.MainFrame;

import javax.swing.*;
import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        SwingUtilities.invokeLater(() -> new MainFrame("Student Management"));
    }
}