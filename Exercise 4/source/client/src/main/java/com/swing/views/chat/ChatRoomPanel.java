package com.swing.views.chat;

import com.swing.models.ChatRoom;
import com.swing.models.Message;
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
    protected ChatRoom chatRoom;
    protected List<Message> messages;
    protected Message newMessage;

    protected ChatRoomPanel(String chatId) {
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


        chatRoom = ChatRoom.builder()
                .id(chatId)
                .name(chatId)
                .build();
    }

    public void showUI() {
        setVisible(true);
    }
    public void closeUI() {
        setVisible(false);
    }

    public String getChatName() {
        return chatRoom.getName();
    }
    public String getChatId() {
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