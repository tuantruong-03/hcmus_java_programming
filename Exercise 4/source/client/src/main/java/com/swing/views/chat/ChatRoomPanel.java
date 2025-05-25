package com.swing.views.chat;

import com.swing.callers.ChatRoomCaller;
import com.swing.context.ApplicationContext;
import com.swing.context.AuthContext;
import com.swing.io.Output;
import com.swing.io.chatroom.*;
import com.swing.models.ChatRoom;
import com.swing.models.Message;
import lombok.Getter;
import lombok.extern.java.Log;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

@Log
public abstract class ChatRoomPanel extends JPanel {
    protected JPanel chatArea;
    protected JTextField messageField;
    protected JButton sendButton;
    protected JButton sendFileButton;
    @Getter
    protected transient ChatRoom chatRoom;
    protected List<Message> messages;
    protected Message newMessage;

    protected final transient ChatRoomCaller chatRoomCaller;

    protected ChatRoomPanel(ChatRoom chatRoom) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Chat Title"));
        chatArea = new JPanel();
        chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Input field for typing messages
        messageField = new JTextField();

        // Send button for sending messages
        sendButton = new JButton("Send");

        // Send file button for uploading files
        sendFileButton = new JButton("Send File");

        // Panel for input field and buttons
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.add(sendFileButton, BorderLayout.WEST);

        add(inputPanel, BorderLayout.SOUTH);

        // Add listeners
        sendButton.addActionListener(e -> sendMessage());
        sendFileButton.addActionListener(e -> sendFile());

        chatRoomCaller = ApplicationContext.getInstance().getChatRoomCaller();
        this.chatRoom = chatRoom;
        renderWithData();
    }

    public void renderWithData() {
        String chatRoomId = chatRoom.getId();
        if (chatRoom.isNew()) {
            List<String> userIds = chatRoom.getUserIds();
            List<String> otherUserIds = userIds.stream().
                    filter(userId -> !userId.equals(AuthContext.INSTANCE.getPrincipal().getUserId()) ).toList();
            var inputResult = CreateChatRoomInput.builder()
                    .otherUserIds(otherUserIds.stream().toList().toArray(new String[0]))
                    .isGroup(false)
                    .build();
            if (inputResult.isFailure()) {
                log.warning("ChatRoomPanel::renderWithData: " + inputResult.getException().getMessage());
                return;
            }
            var result = chatRoomCaller.createOne(inputResult.getValue());
            if (result.isFailure()) {
                log.warning("ChatRoomPanel::renderWithData: " + result.getException().getMessage());
                return;
            }
            Output<CreateChatRoomOutput> output = result.getValue();
            if (output.getError() != null) {
                log.warning("ChatRoomPanel::renderWithData: " + result.getException().getMessage());
                return;
            }
            chatRoomId = output.getBody().getChatRoomId();
        }
        // Fetch full data of chatRoom
        GetChatRoomInput input = GetChatRoomInput.builder()
                .chatRoomId(chatRoomId)
                .build();
        var result = chatRoomCaller.getChatRoom(input);
        if (result.isFailure()) {
            log.warning("MainChatPanels::fetchChatRooms: " + result.getException().getMessage());
            return;
        }
        GetChatRoomOutput output = result.getValue().getBody();
        chatRoom.setUserIds(output.getMembers().values().stream().toList());
    }

    public void showUI() {
        setVisible(true);
    }
    public void closeUI() {
        setVisible(false);
    }

    public String getChatRoomName() {
        return chatRoom.getName();
    }
    public String getChatRoomId() {
        return chatRoom.getId();
    }

    protected abstract void sendMessage();


    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a file to send");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            appendMessage("Me: Sent file - " + selectedFile.getName());
        }
    }

    /**
     * Appends a message to the chat area with alignment.
     * @param message The message text.
     */
    protected void appendMessage(String message) {
        boolean isSelf = true;
        JPanel messagePanel = new JPanel(new FlowLayout(isSelf ? FlowLayout.RIGHT : FlowLayout.LEFT));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel messageLabel = new JLabel(message);
        messageLabel.setOpaque(true);
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        messagePanel.add(messageLabel);
        chatArea.add(messagePanel);
        chatArea.revalidate();
        chatArea.repaint();
    }
}