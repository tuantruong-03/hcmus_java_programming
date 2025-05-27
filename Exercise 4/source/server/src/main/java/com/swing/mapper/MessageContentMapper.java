package com.swing.mapper;

import com.swing.io.message.Content;
import com.swing.models.Message;

public class MessageContentMapper {
    private MessageContentMapper() {}

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
                .value(content.getText())
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
                .text(content.getValue())
                .build();
    }
}
