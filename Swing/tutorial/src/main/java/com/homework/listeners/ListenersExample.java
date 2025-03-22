package com.homework.listeners;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

public class ListenersExample {
    public static void main(String[] args) {
        new ActionListenerExample("ActionListenerExample");
        new MouseListenerExample("MouseListenerExample");
        new KeyListenerExample("KeyListenerExample");
        new ItemListenerExample("ItemListenerExample");
        new ChangeListenerExample("ChangeListenerExample");
    }
}

// The ActionListener is used to handle actions like button clicks.
class ActionListenerExample extends JFrame implements ActionListener {
    public ActionListenerExample(String title) {
        super(title);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        JButton clickButton = new JButton("Click Me");
        clickButton.setActionCommand("Click");
        clickButton.addActionListener(this);
        JButton submitButton = new JButton("Submit");
        submitButton.setActionCommand("Submit");
        submitButton.addActionListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.add(clickButton);
        panel.add(submitButton);
        add(panel);
        setSize(300, 200);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "Click":
                JOptionPane.showMessageDialog(this, "Click Me");
                break;
            case "Submit":
                JOptionPane.showMessageDialog(this, "Submit");
                break;
            default:
                break;
        }
    }
}

// The MouseListener & MouseMotionListener handles mouse clicks, movements, and hovering.
class MouseListenerExample extends JFrame implements MouseListener, MouseMotionListener {
    private JLabel label;

    public MouseListenerExample(String title) {
        super(title);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        label = new JLabel("Move or Click the Mouse");
        panel.add(label);

        panel.addMouseListener(this);
        panel.addMouseMotionListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(panel);
        setSize(400, 200);
        setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        label.setText("Mouse Clicked at: " + e.getX() + ", " + e.getY());
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        label.setText("Mouse Entered");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        label.setText("Mouse Exited");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        label.setText("Mouse Pressed");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        label.setText("Mouse Released");
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        label.setText("Mouse Moved at: " + e.getX() + ", " + e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {}

}

// The KeyListener handles keyboard inputs.
class KeyListenerExample extends JFrame implements KeyListener {
    private JTextArea textArea;

    public KeyListenerExample(String title) {
        super(title);
        textArea = new JTextArea("Press any key...", 5, 20);
        textArea.addKeyListener(this);
        textArea.setFocusable(true);

        JPanel panel = new JPanel();
        panel.add(new JScrollPane(textArea));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(panel);
        setSize(400, 200);
        setVisible(true);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        textArea.setText("Key Pressed: " + e.getKeyChar());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        textArea.append("\nKey Released: " + e.getKeyChar());
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}

// TheItemListener handles selection changes in checkboxes and combo boxes.
class ItemListenerExample extends JFrame implements ItemListener {
    private JCheckBox checkBox;
    private JComboBox<String> comboBox;

    public ItemListenerExample(String title) {
        super(title);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        checkBox = new JCheckBox("Accept Terms and Conditions");
        checkBox.addItemListener(this);
        panel.add(checkBox);

        comboBox = new JComboBox<>(new String[]{"Option 1", "Option 2", "Option 3"});
        comboBox.addItemListener(this);
        panel.add(comboBox);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(panel);
        setSize(300, 200);
        setVisible(true);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == checkBox) {
            String message = checkBox.isSelected() ? "Checked" : "Unchecked";
            JOptionPane.showMessageDialog(this, "Checkbox is " + message);
        } else if (e.getSource() == comboBox) {
            JOptionPane.showMessageDialog(this, "Selected: " + comboBox.getSelectedItem());
        }
    }
}

// TheChangeListener handles changes in sliders and progress bars.
class ChangeListenerExample extends JFrame implements ChangeListener {
    private JSlider slider;
    private JProgressBar progressBar;

    public ChangeListenerExample(String title) {
        super(title);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        slider = new JSlider(0, 100, 50);
        slider.addChangeListener(this);
        panel.add(slider);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(slider.getValue());
        panel.add(progressBar);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(panel);
        setSize(300, 200);
        setVisible(true);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        progressBar.setValue(slider.getValue());
    }

    public static void main(String[] args) {
        new ChangeListenerExample("ChangeListenerExample");
    }

}