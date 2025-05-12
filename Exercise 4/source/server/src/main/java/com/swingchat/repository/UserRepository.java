package com.swingchat.repository;

import com.swingchat.database.Database;
import com.swingchat.models.User;
import lombok.extern.java.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Log
public class UserRepository {
    private final Database db;

    public UserRepository(Database db) {
        this.db = db;
    }
    public void createOne(User user) {
        String sql = "INSERT INTO user (id, name, username, password) VALUES (?, ?, ?, ?)";
        int columnIndex = 1;
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(columnIndex++, user.getId());
            stmt.setString(columnIndex++, user.getName());
            stmt.setString(columnIndex++, user.getUsername());
            stmt.setString(columnIndex, user.getPassword());
            stmt.executeUpdate();
            log.info("User created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
