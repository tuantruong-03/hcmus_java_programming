package com.swing.components;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class TableImageCellRenderer extends JLabel implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {

        // Set the image icon for the cell
        if (value != null) {
            ImageIcon icon = (ImageIcon) value;
            int width = table.getColumnModel().getColumn(column).getWidth() * 3/4;
            int height = table.getRowHeight(row) * 3/4; // get row
            int minSize = Math.min(width, height);
            Image resizedImage = icon.getImage().getScaledInstance(minSize, minSize, Image.SCALE_SMOOTH); // Resize the image
            setIcon(new ImageIcon(resizedImage));
        } else {
            setIcon(null);
        }

        // Center the image
        setHorizontalAlignment(SwingConstants.CENTER);
        return this;
    }
}
