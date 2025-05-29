package com.swing.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.events.Event;
import lombok.extern.java.Log;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Log
public class EventDispatcher implements Runnable {

    private final BufferedReader reader;
    private final ObjectMapper mapper;
    private final Map<String,EventObserver> observers = new HashMap<>();

    public EventDispatcher(BufferedReader reader) {
        this.reader = reader;
        this.mapper = new ObjectMapper();
    }

    public EventObserver addObserver(EventObserver observer) {
        if (observers.containsKey(observer.getName())) {
            return observers.get(observer.getName());
        }
        observers.put(observer.getName(), observer);
        return observer;
    }

    public void removeObserver(String name) {
        observers.remove(name);
    }

    public EventObserver getObserver(String name) {
        return observers.get(name);
    }

    private void dispatch(Event event) {
        Event.Type type = event.getType();
        for (EventObserver observer : observers.values()) {
            switch (type) {
                case Event.Type.SEND_MESSAGE, Event.Type.UPDATE_MESSAGE, Event.Type.DELETE_MESSAGE:
                    if (observer instanceof MessageObserver messageObserver) {
                        messageObserver.onEvent(event);
                    }
                    break;
                case USER_LOGIN:
                    if (observer instanceof UserObserver userObserver) {
                        userObserver.onEvent(event);
                    }
                    break;
                case CREATE_CHAT_ROOM:
                    if (observer instanceof ChatRoomObserver chatRoomObserver) {
                        chatRoomObserver.onEvent(event);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void run() {
        String line;
        log.info("EventDispatcher is running...");
        while (true) {
            try {
                log.info("Waiting for line...");
                line = reader.readLine();
                log.info("Received line: " + line);
                Event event = mapper.readValue(line, Event.class);
                log.info("Received event: " + event);
                dispatch(event); // Notify observers
            } catch (IOException e) {
                log.warning("Client disconnected or error: " + e.getMessage());
                break;
            }
        }

    }
}
