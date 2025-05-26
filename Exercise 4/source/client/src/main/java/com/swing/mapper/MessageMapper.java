package com.swing.mapper;

import com.swing.events.Event;
import com.swing.io.message.CreateMessageInput;
import com.swing.models.Message;

public class MessageMapper {
    private MessageMapper() {}

    public static CreateMessageInput fromModelToCreateInput(Message message) {
        return CreateMessageInput.builder()
                .chatRoomId(message.getChatRoomId())
                .receiverIds(message.getReceiverIds())
                .content(MessageContentMapper.fromModelToIO(message.getContent()))
                .build();
    }

    public static Message fromEventToModel(Event.SendMessagePayload payload) {
        return Message.builder()
                .id(payload.getMessageId())
                .chatRoomId(payload.getChatRoomId())
                .senderId(payload.getSenderId())
                .receiverIds(payload.getReceiverIds())
                .createdAt(payload.getCreatedAt())
                .build();
    }
}
