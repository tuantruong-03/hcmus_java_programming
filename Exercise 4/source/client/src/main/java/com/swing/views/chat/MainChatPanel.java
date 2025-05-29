package com.swing.views.chat;

import com.swing.callers.ChatRoomCaller;
import com.swing.context.ApplicationContext;
import com.swing.context.AuthContext;
import com.swing.event.ChatRoomObserver;
import com.swing.event.ObserverName;
import com.swing.event.UserObserver;
import com.swing.io.Output;
import com.swing.io.chatroom.*;
import com.swing.models.ChatRoom;
import com.swing.models.User;
import com.swing.views.MainFrame;
import lombok.extern.java.Log;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log
public class MainChatPanel extends JPanel {
    private JList<ChatRoom> chatRoomList;
    private DefaultListModel<ChatRoom> chatRoomListModel;
    private JButton createGroupButton, logoutButton;
    private JPanel chatPanelContainer, chatListPanel;
    private CardLayout chatCardLayout;
    private Map<String, ChatRoomPanel> chatRoomPanelMap;
    private final MainFrame parent;

    private final transient ChatRoomCaller chatRoomCaller;
    private final transient UserObserver userObserver;
    private final transient ChatRoomObserver chatRoomObserver;


    public MainChatPanel(MainFrame parent) {
        this.parent = parent;
        this.chatRoomCaller = ApplicationContext.getInstance().getChatRoomCaller();
        this.userObserver = new UserObserver(ObserverName.USER_LOGIN_OBSERVER);
        this.chatRoomObserver = new ChatRoomObserver(ObserverName.CHAT_ROOM_OBSERVER);
        render();
        this.userObserver.addOtherLoginConsumer(this::handleOtherUserLogin);
        this.chatRoomObserver.addCreatedChatRoomConsumer(this::handleCreatedChatRoom);
        ApplicationContext.getInstance().getEventDispatcher().addObserver(userObserver);
        ApplicationContext.getInstance().getEventDispatcher().addObserver(chatRoomObserver);
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
                log.warning("MainChatPanels::handleOtherUserLogin: " + output.getError().getMessage());
                return;
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
        List<String> otherUserIds = List.of(user.getId());
        var inputResult = CreateChatRoomInput.builder()
                .otherUserIds(otherUserIds.stream().toList().toArray(new String[0]))
                .isGroup(false)
                .build();
        if (inputResult.isFailure()) {
            log.warning("ChatRoomPanel::handleOtherUserLogin: " + inputResult.getException().getMessage());
            return;
        }
        var result1 = chatRoomCaller.createOne(inputResult.getValue());
        if (result1.isFailure()) {
            log.warning("ChatRoomPanel::handleOtherUserLogin: " + result1.getException().getMessage());
            return;
        }
        Output<CreateChatRoomOutput> output1 = result1.getValue();
        if (output1.getError() != null) {
            log.warning("ChatRoomPanel::handleOtherUserLogin: " + output1.getError().getMessage());
            return;
        }
        String chatRoomId = output1.getBody().getChatRoomId();
        chatRoom.setId(chatRoomId);
        addChatRoom(chatRoom);
        revalidate();
        repaint();
    }

    public void handleCreatedChatRoom(ChatRoom chatRoom) {
        if (chatRoomPanelMap.containsKey(chatRoom.getId())) return;
        addChatRoom(chatRoom);
        revalidate();
        repaint();
    }

    private void render() {
        setSize(800, 600);
        setLayout(new BorderLayout());

        chatRoomListModel = new DefaultListModel<>();
        chatRoomListModel.addElement(ChatRoom.builder()
                .name(String.format("Me (%s)", AuthContext.INSTANCE.getPrincipal().getName()))
                        .isGroup(false)
                .build());
        chatRoomList = new JList<>(chatRoomListModel);
        chatRoomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chatRoomList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ChatRoom selectedChat = chatRoomList.getSelectedValue();
                openChatPanel(selectedChat.getId());
            }
        });
        chatListPanel = new JPanel(new BorderLayout());
        chatCardLayout = new CardLayout();
        chatPanelContainer = new JPanel(chatCardLayout);
        chatRoomPanelMap = new HashMap<>();
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
            addChatRoom(chatRoom);
        }
        chatListPanel.setBorder(BorderFactory.createTitledBorder("Conversations"));
        chatListPanel.add(new JScrollPane(chatRoomList), BorderLayout.CENTER);
        JPanel controlPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        createGroupButton = new JButton("Create Group");
        createGroupButton.addActionListener(e ->
            new CreateChatRoomDialog(this.parent).setVisible(true));
        logoutButton = new JButton("Logout");
        controlPanel.add(createGroupButton);
        controlPanel.add(logoutButton);
        chatListPanel.add(controlPanel, BorderLayout.SOUTH);
        add(chatListPanel, BorderLayout.WEST);
        add(chatPanelContainer, BorderLayout.CENTER);
    }

    private void addChatRoom(ChatRoom chatRoom) {
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
            chatRoom.setName(stringBuilder.toString());
        }
        chatRoomListModel.addElement(chatRoom);
        ChatRoomPanel chatRoomPanel;
        if (chatRoom.isGroup()) {
            chatRoomPanel = new UserChatRoomPanel(chatRoom);
        } else {
            chatRoomPanel = new GroupChatRoomPanel(chatRoom);
        }
        chatRoomPanelMap.put(chatRoom.getId(), chatRoomPanel);
        chatPanelContainer.add(chatRoomPanel, chatRoom.getId());
    }

    public void openChatPanel(String chatRoomId) {
        chatCardLayout.show(chatPanelContainer, chatRoomId);
    }
}