package com.swing.views.chat;


import com.swing.models.ChatRoom;

public class GroupChatRoomPanel extends ChatRoomPanel {

    public GroupChatRoomPanel(ChatRoom chatRoom) {
        super(chatRoom);
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