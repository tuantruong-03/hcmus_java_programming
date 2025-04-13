package com.swing;

import com.swing.context.ApplicationContext;
import com.swing.views.MainFrame;

import javax.swing.*;

public class DictionaryApplication {
    public static void main(String[] args) {
        ApplicationContext.init();
        SwingUtilities.invokeLater(() -> new MainFrame("Việt - Anh / Anh - Việt Dictionary"));
    }
}