package com.swing.event;


import com.swing.events.Event;
import com.swing.models.Message;
import lombok.Getter;

import java.util.List;
import java.util.function.Consumer;

@Getter
public class MessageObserver implements EventObserver {
    private final String name;
    private List<Consumer<Message>> consumers;
    private Message message;

    public MessageObserver(String name) {
        this.message = new Message();
        this.name = name;
    }

    @Override
    public void onEvent(Event event) {
        Event.SendMessagePayload payload = (Event.SendMessagePayload) event.getPayload();
        this.message = Message.builder()
                .id(payload.getMessageId())
                .chatRoomId(payload.getChatRoomId())
                .senderId(payload.getSenderId())
                .senderName(payload.getSenderName())
                .createdAt(payload.getCreatedAt())
                .build();
        for (Consumer<Message> consumer : consumers) {
            consumer.accept(message);
        }
    }

    public void register(Consumer<Message> consumer) {
        consumers.add(consumer);
    }
}

