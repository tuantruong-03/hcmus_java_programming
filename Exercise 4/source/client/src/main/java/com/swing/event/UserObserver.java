package com.swing.event;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.events.Event;
import com.swing.models.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class UserObserver implements EventObserver {
    private final String name;
    private final List<Consumer<User>> consumers;
    private final ObjectMapper objectMapper;
    private User user;

    public UserObserver(String name) {
        this.user = new User();
        this.consumers = new ArrayList<>();
        this.objectMapper = new ObjectMapper();
        this.name = name;
    }

    @Override
    public void onEvent(Event event) {
        Event.LoginPayload payload = objectMapper.convertValue(event.getPayload(), Event.LoginPayload.class);
        this.user = User.builder()
                .id(payload.getUserId())
                .name(payload.getName())
                .username(payload.getUsername())
                .build();
        for (Consumer<User> consumer : consumers) {
            consumer.accept(user);
        }
    }

    public void addOtherLoginConsumer(Consumer<User> consumer) {
        consumers.add(consumer);
    }
}

