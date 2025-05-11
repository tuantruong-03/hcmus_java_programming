package com.swingchat.views.chat;

import com.swingchat.models.Chat;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public abstract class ChatPanel extends JPanel {
    protected JPanel chatArea;
    protected JTextField messageField;
    protected JButton sendButton;
    protected JButton sendFileButton;
    protected Chat chat;

    protected ChatPanel(String chatId) {
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

        chat = Chat.builder()
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
        return chat.getName();
    }
    public String getChatId() {
        return chat.getId();
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
     * @param isSelf Whether the message is from the current user.
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