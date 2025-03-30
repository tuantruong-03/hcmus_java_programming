package com.swing.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
    private Connection connection;
    private final ConnectionOptions connOpts;
    private final Logger log = Logger.getLogger(Database.class.getName());

    public Database(ConnectionOptions connOpts) throws SQLException {
        log.info("Initializing connection to the database...");
        this.connOpts = connOpts;
        this.connection = createConnection();
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            log.info("Reconnecting to the database...");
            connection = createConnection();
        }
        return connection;
    }

    private Connection createConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(connOpts.url, connOpts.username, connOpts.password);
            log.info("Connected to the database.");
            return conn;
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Database connection failed", e);
            throw e; // Rethrow SQLException
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ConnectionOptions {
        private final String url;
        private final String username;
        private final String password;

        public ConnectionOptions(String url, String databaseName, String username, String password) {
            this.url = url + "/" + databaseName;
            this.username = username;
            this.password = password;
        }
    }
}
