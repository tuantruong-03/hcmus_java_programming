package com.swing.context;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.swing.database.Database;
import com.swing.handlers.AuthHandler;
import com.swing.handlers.ChatRoomHandler;
import com.swing.handlers.MessageHandler;
import com.swing.handlers.UserHandler;
import com.swing.publishers.EventPublisher;
import com.swing.repository.ChatRoomRepository;
import com.swing.repository.ChatRoomUserRepository;
import com.swing.repository.MessageRepository;
import com.swing.repository.UserRepository;

import lombok.Getter;
import lombok.extern.java.Log;


@Getter
@Log
public class ApplicationContext {
    private static ApplicationContext context;

    private ObjectMapper objectMapper;
    private UserRepository userRepository;
    private ChatRoomRepository chatRoomRepository;
    private ChatRoomUserRepository chatRoomUserRepository;
    private MessageRepository messageRepository;
    private AuthHandler authHandler;
    private ChatRoomHandler chatRoomHandler;
    private UserHandler userHandler;
    private MessageHandler messageHandler;
    private EventPublisher eventPublisher;

    private ApplicationContext() {
    }

    public static void init(Database database) throws RuntimeException {
        context = new ApplicationContext();
        context.objectMapper = new ObjectMapper();
        context.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        context.userRepository = new UserRepository(database);
        context.chatRoomRepository = new ChatRoomRepository(database);
        context.chatRoomUserRepository = new ChatRoomUserRepository(database);
        context.messageRepository = new MessageRepository(database);
        context.authHandler = new AuthHandler(context.userRepository);
        context.chatRoomHandler = new ChatRoomHandler(context.chatRoomRepository, context.chatRoomUserRepository, context.userRepository);
        context.userHandler = new UserHandler(context.userRepository);
        context.messageHandler = new MessageHandler(context.messageRepository, context.chatRoomRepository, context.chatRoomUserRepository);
        log.info("Application context initialized successfully.");

    }

    public static ApplicationContext getInstance() {
        if (context == null) {
            throw new IllegalStateException("ApplicationContext not initialized");
        }
        return context;
    }

}
