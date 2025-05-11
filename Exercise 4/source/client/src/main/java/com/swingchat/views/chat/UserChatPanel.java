package com.swingchat.views.chat;


public class UserChatPanel extends ChatPanel {

    public UserChatPanel(String chatId) {
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