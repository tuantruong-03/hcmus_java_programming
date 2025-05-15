package com.swing.context;


import com.swing.database.Database;
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
            log.info("Application context initialized successfully.");
        } catch (Exception e) {
            log.info(e.getMessage());
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
