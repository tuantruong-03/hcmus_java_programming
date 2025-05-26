package com.swing.event;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.events.Event;
import com.swing.mapper.MessageContentMapper;
import com.swing.models.Message;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class MessageObserver implements EventObserver {
    private final String name;
    private final List<Consumer<Message>> receivedMessageConsumers;
    private final List<Consumer<Message>> deletedMessageConsumers;
    private final List<Consumer<Message>> updatedMessageConsumers;
    private Message message;
    private final ObjectMapper objectMapper;

    public MessageObserver(String name) {
        this.message = new Message();
        this.receivedMessageConsumers = new ArrayList<>();
        this.deletedMessageConsumers = new ArrayList<>();
        this.updatedMessageConsumers = new ArrayList<>();
        this.name = name;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void onEvent(Event event) {
        switch (event.getType()) {
            case SEND_MESSAGE:
                Event.SendMessagePayload smp =  objectMapper.convertValue(event.getPayload(), Event.SendMessagePayload.class);
                this.message = Message.builder()
                        .id(smp.getMessageId())
                        .chatRoomId(smp.getChatRoomId())
                        .senderId(smp.getSenderId())
                        .senderName(smp.getSenderName())
                        .content(MessageContentMapper.fromEventToModel(smp.getContent()))
                        .createdAt(smp.getCreatedAt())
                        .build();
                for (Consumer<Message> consumer : receivedMessageConsumers) {
                    consumer.accept(message);
                }
                break;
            case UPDATE_MESSAGE:
                Event.UpdateMessagePayload ump =  objectMapper.convertValue(event.getPayload(), Event.UpdateMessagePayload.class);
                this.message = Message.builder()
                        .id(ump.getMessageId())
                        .chatRoomId(ump.getChatRoomId())
                        .content(MessageContentMapper.fromEventToModel(ump.getContent()))
                        .build();
                for (Consumer<Message> consumer : updatedMessageConsumers) {
                    consumer.accept(message);
                }
                break;
            case DELETE_MESSAGE:
                Event.DeleteMessagePayload dmp =  objectMapper.convertValue(event.getPayload(), Event.DeleteMessagePayload.class);
                this.message = Message.builder()
                        .id(dmp.getMessageId())
                        .chatRoomId(dmp.getChatRoomId())
                        .build();
                for (Consumer<Message> consumer : deletedMessageConsumers) {
                    consumer.accept(message);
                }
                break;
        }

    }

    public void addReceivedMessageConsumer(Consumer<Message> consumer) {
        receivedMessageConsumers.add(consumer);
    }

    public void addDeletedMessageConsumer(Consumer<Message> consumer) {
        deletedMessageConsumers.add(consumer);
    }

    public void addUpdatedMessageConsumer(Consumer<Message> consumer) {
        updatedMessageConsumers.add(consumer);
    }
}

