package com.swing.database;

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
            Class.forName(connOpts.driver);
            Connection conn = DriverManager.getConnection(connOpts.url, connOpts.user, connOpts.password);
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
        private final String driver;
        private final String url;
        private final String user;
        private final String password;

        public ConnectionOptions(String driver, String url, String databaseName, String user, String password) {
            this.driver = driver;
            this.url = url + "/" + databaseName;
            this.user = user;
            this.password = password;
        }
    }
}
