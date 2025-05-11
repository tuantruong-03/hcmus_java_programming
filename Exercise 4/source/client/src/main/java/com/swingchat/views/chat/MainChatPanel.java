package com.swingchat.views.chat;

import com.swingchat.views.MainFrame;

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
    private List<ChatPanel> chatPanels;

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

        chatPanels = new ArrayList<>();
        ChatPanel group1ChatPanel = new GroupChatPanel("Group 1");
        ChatPanel user1ChatPanel = new UserChatPanel("User 1");
        ChatPanel user2ChatPanel = new UserChatPanel("User 2");
        // Seed data
        chatPanels.add(group1ChatPanel);
        chatPanels.add(user1ChatPanel);
        chatPanels.add(user2ChatPanel);
        chatPanelContainer.add(group1ChatPanel, group1ChatPanel.getChatId());
        chatPanelContainer.add(user1ChatPanel, user1ChatPanel.getChatId());
        chatPanelContainer.add(user2ChatPanel, user2ChatPanel.getChatId());

        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedChat = userList.getSelectedValue();
                openChatPanel(selectedChat);
            }
        });

        setVisible(true);
    }

    public void openChatPanel(String chatId) {
        ChatPanel chatPanel = chatPanels.stream().filter(cp -> cp.getChatName().equals(chatId)).findFirst().orElse(null);
        if (chatPanel != null) {
            chatCardLayout.show(chatPanelContainer, chatPanel.getChatId());
        }
    }

}