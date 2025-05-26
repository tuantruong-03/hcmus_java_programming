package com.swing.mapper;

import com.swing.events.MessageContent;
import com.swing.io.message.Content;
import com.swing.models.Message;

public class MessageContentMapper {
    private MessageContentMapper() {}

    public static Message.Content fromEventToModel(MessageContent content) {
        Message.Content.Type type = null;
        if (content.getType() != null) {
            if (content.getType() == MessageContent.Type.TEXT) {
                type = Message.Content.Type.TEXT;
            } else if (content.getType() == MessageContent.Type.FILE) {
                type = Message.Content.Type.FILE;
            }
        }
        return Message.Content.builder()
                .value(content.getValue())
                .type(type)
                .build();
    }
    public static Content fromModelToIO(Message.Content content) {
        Content.Type type = null;
        if (content.getType() != null) {
            if (content.getType() == Message.Content.Type.TEXT) {
                type = Content.Type.TEXT;
            } else if (content.getType() == Message.Content.Type.FILE) {
                type = Content.Type.FILE;
            }
        }
        return Content.builder()
                .type(type)
                .value(content.getValue())
                .build();
    }

    public static Message.Content fromIOToModel(Content content) {
        Message.Content.Type type = null;
        if (content.getType() != null) {
            if (content.getType() == Content.Type.TEXT) {
                type = Message.Content.Type.TEXT;
            } else if (content.getType() == Content.Type.FILE) {
                type = Message.Content.Type.FILE;
            }
        }
        return Message.Content.builder()
                .type(type)
                .value(content.getValue())
                .build();
    }
}
