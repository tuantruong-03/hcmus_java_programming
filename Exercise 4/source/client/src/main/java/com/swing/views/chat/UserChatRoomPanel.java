package com.swing.views.chat;


import com.swing.models.ChatRoom;

public class UserChatRoomPanel extends ChatRoomPanel {

    public UserChatRoomPanel(ChatRoom chatRoom) {
        super(chatRoom);
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