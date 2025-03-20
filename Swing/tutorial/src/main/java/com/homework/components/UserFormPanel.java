package com.homework.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserFormPanel extends JPanel implements ActionListener {
    private final JTextField nameField;
    private final JPasswordField passwordField;
    private final JRadioButton maleRadio;
    private final JRadioButton femaleRadio;
    private final JComboBox<String> countryComboBox;
    private final JList<String> skillsList;
    private final JCheckBox readingCheckBox, gamingCheckBox, sportsCheckBox;
    private final JTextArea commentArea;
    private final JButton submitButton;

    public UserFormPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Name Field
        add(createLabeledComponent("Name:", nameField = new JTextField(15)));

        // Password Field
        add(createLabeledComponent("Password:", passwordField = new JPasswordField(15)));

        // Gender Selection
        JPanel genderPanel = new JPanel();
        genderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        add(createLabeledComponent("Gender:", genderPanel));

        // Country Dropdown
        String[] countries = {"USA", "UK", "Canada", "Australia", "Other"};
        countryComboBox = new JComboBox<>(countries);
        add(createLabeledComponent("Country:", countryComboBox));

        // Skills List
        String[] skills = {"Java", "Python", "C++", "JavaScript", "SQL"};
        skillsList = new JList<>(skills);
        skillsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        add(createLabeledComponent("Skills:", new JScrollPane(skillsList)));

        // Hobbies Checkboxes
        JPanel hobbyPanel = new JPanel();
        hobbyPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        readingCheckBox = new JCheckBox("Reading");
        gamingCheckBox = new JCheckBox("Gaming");
        sportsCheckBox = new JCheckBox("Sports");
        hobbyPanel.add(readingCheckBox);
        hobbyPanel.add(gamingCheckBox);
        hobbyPanel.add(sportsCheckBox);
        add(createLabeledComponent("Hobbies:", hobbyPanel));

        // Comments Text Area
        commentArea = new JTextArea(3, 20);
        add(createLabeledComponent("Comments:", new JScrollPane(commentArea)));

        // Submit Button
        submitButton = new JButton("Submit");
        submitButton.addActionListener(this);
        add(submitButton);
    }

    private JPanel createLabeledComponent(String label, JComponent component) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(label));
        panel.add(component);
        return panel;
    }

    public String getName() { return nameField.getText(); }

    public String getPassword() { return new String(passwordField.getPassword()); }

    public String getGender() { return maleRadio.isSelected() ? "Male" : femaleRadio.isSelected() ? "Female" : "Not Selected"; }

    public String getCountry() { return (String) countryComboBox.getSelectedItem(); }

    public List<String> getSelectedSkills() { return skillsList.getSelectedValuesList(); }

    public String getHobbies() {
        return (readingCheckBox.isSelected() ? "Reading " : "") +
                (gamingCheckBox.isSelected() ? "Gaming " : "") +
                (sportsCheckBox.isSelected() ? "Sports" : "");
    }

    public String getComments() { return commentArea.getText(); }

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(this,
                "Name: " + getName() + "\n" +
                        "Password: " + getPassword() + "\n" +
                        "Gender: " + getGender() + "\n" +
                        "Country: " + getCountry() + "\n" +
                        "Skills: " + getSelectedSkills() + "\n" +
                        "Hobbies: " + getHobbies() + "\n" +
                        "Comments: " + getComments(),
                "Form Submission", JOptionPane.INFORMATION_MESSAGE);
    }
}