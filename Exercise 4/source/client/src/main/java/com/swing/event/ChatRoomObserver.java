package com.swing.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.events.Event;
import com.swing.models.ChatRoom;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChatRoomObserver implements EventObserver {
    @Getter
    private final String name;
    private final List<Consumer<ChatRoom>> createdChatRoomConsumers;

    private final ObjectMapper objectMapper;

    public ChatRoomObserver(String name) {
        this.name = name;
        this.createdChatRoomConsumers = new ArrayList<>();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void onEvent(Event event) {
        switch (event.getType()) {
            case CREATE_CHAT_ROOM:
                Event.CreateChatRoomPayload ccrp =  objectMapper.convertValue(event.getPayload(), Event.CreateChatRoomPayload.class);
                ChatRoom chatRoom = ChatRoom.builder()
                        .id(ccrp.getChatRoomId())
                        .name(ccrp.getChatRoomName())
                        .isGroup(ccrp.isGroup())
                        .isNew(true)
                        .build();
                for (Consumer<ChatRoom> consumer : createdChatRoomConsumers) {
                    consumer.accept(chatRoom);
                }
                break;
        }

    }

    public void addCreatedChatRoomConsumer(Consumer<ChatRoom> consumer) {
        this.createdChatRoomConsumers.add(consumer);
    }
}
