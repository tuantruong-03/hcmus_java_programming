package com.swing.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.events.Event;
import lombok.extern.java.Log;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Log
public class EventDispatcher {

    private final BufferedReader reader;
    private final ObjectMapper mapper;
    private final Map<String,EventObserver> observers = new HashMap<>();

    public EventDispatcher(Reader reader) {
        this.reader = new BufferedReader(reader);
        this.mapper = new ObjectMapper();
    }

    public void addObserver(EventObserver observer) {
        observers.put(observer.getName(), observer);
    }

    public void removeObserver(String name) {
        observers.remove(name);
    }

    public EventObserver getObserver(String name) {
        return observers.get(name);
    }

    private void notify(Event event) {
        Event.Type type = event.getType();
        for (EventObserver observer : observers.values()) {
            switch (type) {
                case Event.Type.SEND_MESSAGE:
                    if (observer instanceof MessageObserver messageObserver) {
                        messageObserver.onEvent(event);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void run() {
        String line;
        while (true) {
            try {
                line = reader.readLine();
                Event event = mapper.readValue(line, Event.class);
                notify(event); // Notify observers
            } catch (IOException e) {
                log.info("Client disconnected or error: " + e.getMessage());
                break;
            }
        }

    }

}
