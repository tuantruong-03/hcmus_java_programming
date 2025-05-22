package com.swing.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.events.Event;
import lombok.extern.java.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Log
public class EventDispatcher {

    private final BufferedReader reader;
    private final ObjectMapper mapper;
    private final List<EventObserver> observers = new ArrayList<>();

    public EventDispatcher(Reader reader, EventObserver ...eventObservers) {
        this.reader = new BufferedReader(reader);
        this.observers.addAll(List.of(eventObservers));
        this.mapper = new ObjectMapper();
    }

    public void addObserver(EventObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(EventObserver observer) {
        observers.remove(observer);
    }

    private void notify(Event event) {
        for (EventObserver observer : observers) {
            observer.onEvent(event);
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
