package com.swing.components;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.EventObject;

public class TableActionCellEditor extends AbstractCellEditor implements TableCellEditor {
    private JPanel panel;
    private JTable table;
    private int row;

    public TableActionCellEditor(JTable table) {
        this.table = table;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.row = row;
        if (value instanceof JPanel panel) {
            this.panel = panel;
            return panel;
        }
        return new JPanel();
    }

    @Override
    public Object getCellEditorValue() {
        return panel;
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        return true;
    }
}