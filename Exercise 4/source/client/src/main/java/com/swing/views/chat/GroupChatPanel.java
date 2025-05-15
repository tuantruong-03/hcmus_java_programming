package com.swing.views.chat;



public class GroupChatPanel extends ChatPanel {

    public GroupChatPanel(String chatId) {
        super(chatId);
    }

    // Implement sending message for group chat
    @Override
    protected void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            appendMessage(message);
            messageField.setText("");
        }
    }
}