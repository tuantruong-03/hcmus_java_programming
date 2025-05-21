package com.swing.context;


import com.swing.database.Database;
import com.swing.handlers.AuthHandler;
import com.swing.handlers.ChatRoomHandler;
import com.swing.publishers.EventPublisher;
import com.swing.repository.ChatRoomRepository;
import com.swing.repository.ChatRoomUserRepository;
import com.swing.repository.MessageRepository;
import com.swing.repository.UserRepository;

import lombok.Getter;
import lombok.extern.java.Log;

import java.io.InputStream;
import java.util.Properties;

@Getter
@Log
public class ApplicationContext {
    private static ApplicationContext context;

    private UserRepository userRepository;
    private ChatRoomRepository chatRoomRepository;
    private ChatRoomUserRepository chatRoomUserRepository;
    private MessageRepository messageRepository;
    private AuthHandler authHandler;
    private ChatRoomHandler chatRoomHandler;

    private EventPublisher eventPublisher;

    private ApplicationContext() {}

    public static void init() throws RuntimeException {
        context = new ApplicationContext();

        try (InputStream input = Database.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties props = new Properties();
            if (input == null) {
                throw new RuntimeException("Unable to find application.properties");
            }

            props.load(input);
            String url = props.getProperty("jdbc.url");
            String database = props.getProperty("jdbc.database");
            String user = props.getProperty("jdbc.user");
            String password = props.getProperty("jdbc.password");
            String driver = props.getProperty("jdbc.driver");
            Database.ConnectionOptions options = new Database.ConnectionOptions(
                    driver,
                    url,
                    database,
                    user,
                    password
            );
            Database db = new Database(options);
            context.userRepository = new UserRepository(db);
            context.chatRoomRepository = new ChatRoomRepository(db);
            context.chatRoomUserRepository = new ChatRoomUserRepository(db);
            context.messageRepository = new MessageRepository(db);
            context.authHandler = new AuthHandler(context.userRepository);
            context.chatRoomHandler = new ChatRoomHandler(context.chatRoomRepository, context.chatRoomUserRepository);
            log.info("Application context initialized successfully.");
        } catch (Exception e) {
            log.warning(e.getMessage());
            throw new RuntimeException("Failed to initialize ApplicationContext", e);
        }
    }

    public static ApplicationContext getInstance() {
        if (context == null) {
            throw new IllegalStateException("ApplicationContext not initialized");
        }
        return context;
    }

}
