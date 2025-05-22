package com.swing.views.chat;

import com.swing.views.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainChatPanel extends JPanel {
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JButton createGroupButton, viewHistoryButton, logoutButton;
    private JPanel chatPanelContainer;
    private CardLayout chatCardLayout;
    private List<ChatRoomPanel> chatRoomPanels;

    public MainChatPanel(MainFrame parent) {
        setSize(800, 600);
        setLayout(new BorderLayout());

        userListModel = new DefaultListModel<>();
        userListModel.addElement("User 1");
        userListModel.addElement("User 2");
        userListModel.addElement("Group 1");
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel chatListPanel = new JPanel(new BorderLayout());
        chatListPanel.setBorder(BorderFactory.createTitledBorder("Users & Groups"));
        chatListPanel.add(new JScrollPane(userList), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        createGroupButton = new JButton("Create Group");
        viewHistoryButton = new JButton("View History");
        logoutButton = new JButton("Logout");
        controlPanel.add(createGroupButton);
        controlPanel.add(viewHistoryButton);
        controlPanel.add(logoutButton);
        chatListPanel.add(controlPanel, BorderLayout.SOUTH);

        add(chatListPanel, BorderLayout.WEST);

        chatCardLayout = new CardLayout();
        chatPanelContainer = new JPanel(chatCardLayout);
        add(chatPanelContainer, BorderLayout.CENTER);

        chatRoomPanels = new ArrayList<>();
        ChatRoomPanel group1ChatRoomPanel = new GroupChatRoomPanel("Group 1");
        ChatRoomPanel user1ChatRoomPanel = new UserChatRoomPanel("User 1");
        ChatRoomPanel user2ChatRoomPanel = new UserChatRoomPanel("User 2");
        // Seed data
        chatRoomPanels.add(group1ChatRoomPanel);
        chatRoomPanels.add(user1ChatRoomPanel);
        chatRoomPanels.add(user2ChatRoomPanel);
        chatPanelContainer.add(group1ChatRoomPanel, group1ChatRoomPanel.getChatId());
        chatPanelContainer.add(user1ChatRoomPanel, user1ChatRoomPanel.getChatId());
        chatPanelContainer.add(user2ChatRoomPanel, user2ChatRoomPanel.getChatId());

        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedChat = userList.getSelectedValue();
                openChatPanel(selectedChat);
            }
        });

        setVisible(true);
    }

    public void openChatPanel(String chatId) {
        ChatRoomPanel chatRoomPanel = chatRoomPanels.stream().filter(cp -> cp.getChatName().equals(chatId)).findFirst().orElse(null);
        if (chatRoomPanel != null) {
            chatCardLayout.show(chatPanelContainer, chatRoomPanel.getChatId());
        }
    }

}