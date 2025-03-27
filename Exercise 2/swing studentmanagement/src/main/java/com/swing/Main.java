package com.swing;

import com.swing.views.MainFrame;

import javax.swing.*;
import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException {
        new MainFrame("Student Management");
    }
}