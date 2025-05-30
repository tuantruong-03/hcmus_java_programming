package com.swing.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Database {
    private final HikariDataSource dataSource;
    private final Logger log = Logger.getLogger(Database.class.getName());

    public Database(ConnectionOptions connOpts) {
        log.info("Initializing HikariCP connection pool...");

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(connOpts.driver);
        config.setJdbcUrl(connOpts.url + "/" + connOpts.databaseName);
        config.setUsername(connOpts.user);
        config.setPassword(connOpts.password);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(60000);
        config.setConnectionTimeout(30000);
        config.setLeakDetectionThreshold(2000);

        this.dataSource = new HikariDataSource(config);
        log.info("HikariCP connection pool initialized.");
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection(); // Each call gets a pooled connection
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
            log.info("HikariCP connection pool closed.");
        }
    }

    public static class ConnectionOptions {
        private final String driver;
        private final String url;
        private final String databaseName;
        private final String user;
        private final String password;

        public ConnectionOptions(String driver, String url, String databaseName, String user, String password) {
            this.driver = driver;
            this.url = url;
            this.databaseName = databaseName;
            this.user = user;
            this.password = password;
        }
    }
}
