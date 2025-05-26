package com.swing.views.chat;

import com.swing.callers.ChatRoomCaller;
import com.swing.context.ApplicationContext;
import com.swing.context.AuthContext;
import com.swing.event.ObserverName;
import com.swing.event.UserLoginObserver;
import com.swing.io.Output;
import com.swing.io.chatroom.CheckChatRoomExistenceInput;
import com.swing.io.chatroom.GetChatRoomsInput;
import com.swing.io.chatroom.GetChatRoomsOutput;
import com.swing.models.ChatRoom;
import com.swing.models.User;
import com.swing.views.MainFrame;
import lombok.extern.java.Log;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Log
public class MainChatPanel extends JPanel {
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JButton createGroupButton, logoutButton;
    private JPanel chatPanelContainer, chatListPanel;
    private CardLayout chatCardLayout;
    private List<ChatRoomPanel> chatRoomPanels;

    private final transient ChatRoomCaller chatRoomCaller;
    private final transient UserLoginObserver userLoginObserver;


    public MainChatPanel(MainFrame parent) {
        chatRoomCaller = ApplicationContext.getInstance().getChatRoomCaller();
        userLoginObserver = new UserLoginObserver(ObserverName.UserLoginObserver);
        render();
        userLoginObserver.register(this::handleOtherUserLogin);
        ApplicationContext.getInstance().getEventDispatcher().addObserver(userLoginObserver);
        setVisible(true);
    }

    public void handleOtherUserLogin(User user) {
        String myUserId = AuthContext.INSTANCE.getPrincipal().getUserId();
        if (user.getId().equals(myUserId)) return;
        boolean chatRoomExists = true;
        var result = chatRoomCaller.checkChatRoomExistence(
                CheckChatRoomExistenceInput.builder()
                        .userIds(List.of(user.getId(), myUserId))
                        .isGroup(false)
                        .build());
        if (result.isFailure()) {
            log.warning("MainChatPanels::handleOtherUserLogin: " + result.getException().getMessage());
            return;
        }
        var output = result.getValue();
        if (output.getError() != null) {
            if (output.getError().getCode() == Output.Error.Code.NOT_FOUND) {
                chatRoomExists = false;
            } else {
                log.warning("MainChatPanels::handleOtherUserLogin: " + result.getException().getMessage());
                return;
            }
        }
        for (ChatRoomPanel chatRoomPanel : chatRoomPanels) {
            ChatRoom chatRoom = chatRoomPanel.getChatRoom();
            List<String> userIds = chatRoom.getUserIds();
            if (!chatRoom.isGroup() && userIds.contains(user.getId())) {
                chatRoomExists = true;
                break;
            }
        }
        if (chatRoomExists) return;
        AuthContext.Principal principal = AuthContext.INSTANCE.getPrincipal();
        List<String> userIds = List.of(user.getId(), principal.getUserId());
        ChatRoom chatRoom = ChatRoom.builder()
                .name(user.getName())
                .userIds(userIds)
                .isGroup(false)
                .isNew(true)
                .build();
        ChatRoomPanel chatRoomPanel = new UserChatRoomPanel(chatRoom);
        chatRoomPanels.add(chatRoomPanel);
        // If chat room is not group, chatRoomName will be the name of the other user
        chatPanelContainer.add(chatRoomPanel, user.getName());
        userListModel.addElement(user.getName());
        revalidate();
        repaint();
    }

    private void render() {
        setSize(800, 600);
        setLayout(new BorderLayout());

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedChat = userList.getSelectedValue();
                openChatPanel(selectedChat);
            }
        });
        chatListPanel = new JPanel(new BorderLayout());
        chatCardLayout = new CardLayout();
        chatPanelContainer = new JPanel(chatCardLayout);
        chatRoomPanels = new ArrayList<>();
        GetChatRoomsInput input = GetChatRoomsInput.builder()
                .limit(100)
                .page(0)
                .build();
        var result = chatRoomCaller.getMyChatRooms(input);
        if (result.isFailure()) {
            log.warning("MainChatPanels::fetchChatRooms: " + result.getException().getMessage());
            return;
        }
        GetChatRoomsOutput output = result.getValue().getBody();
        List<GetChatRoomsOutput.Item> items = output.getItems();
        List<ChatRoom> chatRooms = items.stream()
                .map(item -> ChatRoom.builder()
                        .id(item.getChatRoomId())
                        .name(item.getChatRoomName())
                        .isGroup(item.isGroup())
                        .createdAt(item.getCreatedAt())
                        .updatedAt(item.getUpdatedAt())
                        .build())
                .toList();
        for (ChatRoom chatRoom : chatRooms) {
            String chatRoomName = chatRoom.getName(); // "Tuan Truong,Jane Bach"
            if (!chatRoom.isGroup()) {
                AuthContext.Principal principal = AuthContext.INSTANCE.getPrincipal();
                String myName = principal.getName(); // "Tuan Truong"
                StringBuilder stringBuilder = new StringBuilder();
                String[] names = chatRoomName.split(",");
                for (String name : names) {
                    if (!name.trim().equals(myName)) {
                        stringBuilder.append(name).append(", ");
                    }
                }
                stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
                chatRoomName = stringBuilder.toString();
            }
            userListModel.addElement(chatRoomName);
            ChatRoomPanel chatRoomPanel;
            if (chatRoom.isGroup()) {
                chatRoomPanel = new UserChatRoomPanel(chatRoom);
            } else {
                chatRoomPanel = new GroupChatRoomPanel(chatRoom);
            }
            chatRoomPanels.add(chatRoomPanel);
            // If chat room is not group, chatRoomName will be the name of the other user
            chatPanelContainer.add(chatRoomPanel, chatRoomPanel.getChatRoomName());
        }

        chatListPanel.setBorder(BorderFactory.createTitledBorder("Conversations"));
        chatListPanel.add(new JScrollPane(userList), BorderLayout.CENTER);
        JPanel controlPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        createGroupButton = new JButton("Create Group");
        logoutButton = new JButton("Logout");
        controlPanel.add(createGroupButton);
        controlPanel.add(logoutButton);
        chatListPanel.add(controlPanel, BorderLayout.SOUTH);
        add(chatListPanel, BorderLayout.WEST);
        add(chatPanelContainer, BorderLayout.CENTER);
    }

    public void openChatPanel(String chatRoomName) {
        chatRoomPanels.stream().filter(cp -> cp.getChatRoomName().equals(chatRoomName)).findFirst().ifPresent(chatRoomPanel -> chatCardLayout.show(chatPanelContainer, chatRoomName));
    }
}