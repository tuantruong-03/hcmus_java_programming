package com.swing.publishers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Writer;

public class EventPublisher {
    private final Writer writer;
    private final ObjectMapper objectMapper;
    public EventPublisher(Writer writer) {
        this.writer = writer;
        this.objectMapper = new ObjectMapper();
    }

    public Exception publish(Object event) {
        try {
            String jsonResponse = objectMapper.writeValueAsString(event);
            writer.write(jsonResponse);
            writer.flush();
        } catch (IOException e) {
            return e;
        }
        return null;
    }
}
