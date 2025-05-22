package com.swing.views.chat;



public class GroupChatRoomPanel extends ChatRoomPanel {

    public GroupChatRoomPanel(String chatId) {
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