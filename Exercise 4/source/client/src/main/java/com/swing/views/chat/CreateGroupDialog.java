package com.swing.views.chat;

import javax.swing.*;
import java.awt.*;

public class CreateGroupDialog extends JDialog {
    private JTextField groupNameField;
    private JButton createButton;
    private MainChatPanel _parent;
    public CreateGroupDialog(MainChatPanel _parent) {
        super();
        setSize(300, 150);
        setLayout(new GridLayout(2, 1));

        groupNameField = new JTextField();
        createButton = new JButton("Create Group");

        add(new JLabel("Group Name:"));
        add(groupNameField);
        add(createButton);

        createButton.addActionListener(e -> createGroup());

        setVisible(true);
    }

    private void createGroup() {
        String groupName = groupNameField.getText();
//        if (!groupName.isEmpty()) {
//            parent.openGroupChat(groupName);
//            dispose();
//        }
    }
}
