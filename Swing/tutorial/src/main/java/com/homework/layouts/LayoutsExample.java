package com.homework.layouts;

import javax.swing.*;
import java.awt.*;

public class LayoutsExample {
    public static void main(String[] args) {
        new FlowLayoutExample().show();
        new BorderLayoutExample().show();
        new GridLayoutExample().show();
        new BoxLayoutExample().show();
        new GridBagLayoutExample().show();
    }
}

// FlowLayout arranges components in a row, wrapping to the next line if needed.
class FlowLayoutExample {
    public void show() {
        JFrame frame = new JFrame("FlowLayout Example");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout()); // Default alignment (left-to-right)

        panel.add(new JButton("Button 1"));
        panel.add(new JButton("Button 2"));
        panel.add(new JButton("Button 3"));
        panel.add(new JButton("Button 4"));
        panel.add(new JButton("Button 5"));

        frame.add(panel);
        frame.setVisible(true);
    }
}

// BorderLayout divides the container into five areas: NORTH, SOUTH, EAST, WEST, CENTER.
class BorderLayoutExample {
    public void show()  {
        JFrame frame = new JFrame("BorderLayout Example");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLayout(new BorderLayout());

        frame.add(new JButton("North"), BorderLayout.NORTH);
        frame.add(new JButton("South"), BorderLayout.SOUTH);
        frame.add(new JButton("East"), BorderLayout.EAST);
        frame.add(new JButton("West"), BorderLayout.WEST);
        frame.add(new JButton("Center"), BorderLayout.CENTER);

        frame.setVisible(true);
    }
}

// GridLayout arranges components in a table-like structure.
class GridLayoutExample {
    public void show() {
        JFrame frame = new JFrame("GridLayout Example");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLayout(new GridLayout(2, 3)); // 2 rows, 3 columns

        for (int i = 1; i <= 6; i++) {
            frame.add(new JButton("Button " + i));
        }

        frame.setVisible(true);
    }
}

// BoxLayout arranges components vertically or horizontally.
class BoxLayoutExample {
    public void show() {
        JFrame frame = new JFrame("BoxLayout Example");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Vertical layout

        panel.add(new JButton("Button 1"));
        panel.add(new JButton("Button 2"));
        panel.add(new JButton("Button 3"));

        frame.add(panel);
        frame.setVisible(true);
    }
}

// GridBagLayout allows fine-tuned component placement with constraints.
class GridBagLayoutExample {
    public void show() {
        JFrame frame = new JFrame("GridBagLayout Example");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5); // Padding

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JButton("Button 1"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(new JButton("Button 2"), gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(new JButton("Button 3 (Wide)"), gbc);

        frame.add(panel);
        frame.setVisible(true);
    }
}