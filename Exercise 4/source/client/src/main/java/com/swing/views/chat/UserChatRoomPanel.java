package com.swing.views.chat;


public class UserChatRoomPanel extends ChatRoomPanel {

    public UserChatRoomPanel(String chatId) {
        super(chatId);
    }

    // Implement sending message for user-to-user chat
    @Override
    protected void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            appendMessage(message);
            messageField.setText("");
        }
    }


}