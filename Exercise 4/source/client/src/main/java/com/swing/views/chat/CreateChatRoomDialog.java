package com.swing.views.chat;


import com.swing.callers.ChatRoomCaller;
import com.swing.callers.UserCaller;
import com.swing.context.ApplicationContext;
import com.swing.context.AuthContext;
import com.swing.io.chatroom.CreateChatRoomInput;
import com.swing.io.user.GetUsersInput;
import com.swing.io.user.GetUsersOutput;
import com.swing.models.User;
import lombok.extern.java.Log;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Log
public class CreateChatRoomDialog extends JDialog {
    private List<User> users;
    private final transient UserCaller userCaller;
    private final transient ChatRoomCaller chatRoomCaller;
    public CreateChatRoomDialog(JFrame parent) {
        super(parent,"Create Chat Room", true);
        this.userCaller = ApplicationContext.getInstance().getUserCaller();
        this.chatRoomCaller = ApplicationContext.getInstance().getChatRoomCaller();
        render();
    }

    private void render() {
        setSize(400, 300);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());
        // Main panel
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        var result1 = userCaller.getMany(GetUsersInput.builder()
                        .page(0)
                .build());
        if (result1.isFailure()) {
            log.warning("CreateChatRoomPanel::render: " + result1.getException().getMessage());
            return;
        }
        var output1 = result1.getValue();
        if (output1.getError() != null) {
            log.warning("CreateChatRoomPanel::render: " + output1.getError().getMessage());
            return;
        }
        GetUsersOutput userOutputs = output1.getBody();
        List<GetUsersOutput.Item> items = userOutputs.getItems();
        users = items.stream()
                .filter(i -> !i.getId().equals(AuthContext.INSTANCE.getPrincipal().getUserId()))
                .map(i -> User
                        .builder()
                        .id(i.getId())
                        .name(i.getName())
                        .username(i.getUsername())
                        .build())
                .toList();
        JList<User> userList = new JList<>(users.toArray(new User[0]));
        userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(userList);
        listScrollPane.setPreferredSize(new Dimension(200, 100));
        panel.add(new JLabel("Select users to add to the group:"), BorderLayout.NORTH);
        panel.add(listScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        JTextField groupNameField = new JTextField();
        JButton createGroupButton = new JButton("Create Group");

        bottomPanel.add(new JLabel("Enter Group Name:"));
        bottomPanel.add(groupNameField);
        bottomPanel.add(createGroupButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);


        createGroupButton.addActionListener(e -> {
            String groupName = groupNameField.getText().trim();
            List<User> selectedUsers = userList.getSelectedValuesList();

            if (groupName.isEmpty() || selectedUsers.isEmpty()) {
                JOptionPane.showMessageDialog(getParent(),
                        "Please enter a group name and select at least one user.",
                        "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<String> memberIds = new ArrayList<>(selectedUsers.stream().map(User::getId).toList());
            var result = chatRoomCaller.createOne(CreateChatRoomInput.builder()
                    .otherUserIds(memberIds.toArray(new String[0]))
                            .name(groupName)
                            .isGroup(true)
                    .build().getValue());
            if (result.isFailure()) {
                log.warning("CreateChatRoomPanel::handleCreateGroupButton: " + result.getException().getMessage());
                return;
            }
            var output = result.getValue();
            JOptionPane.showMessageDialog(getParent(),
                    "Group '" + groupName + "' created with users: " + selectedUsers);
        });
        setContentPane(panel);
    }
}
