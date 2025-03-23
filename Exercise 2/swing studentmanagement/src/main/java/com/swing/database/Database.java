package com.swing.database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private final Connection connection;
    public Database(ConnectionOptions connOpts) throws SQLException  {

           connection = DriverManager.getConnection(connOpts.url, connOpts.username, connOpts.password);
    }
    public Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("Database not open");
        }
        return connection;
    }

    public static class ConnectionOptions {
        private String url;
        private String username;
        private String password;

        public ConnectionOptions(String url, String databaseName,  String username, String password) {
            this.url = url + "/" + databaseName;
            this.username = username;
            this.password = password;
        }
    }
}
