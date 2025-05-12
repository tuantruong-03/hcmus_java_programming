package com.swingchat.repository;

import com.swingchat.database.Database;
import com.swingchat.models.User;
import com.swingchat.repository.query.Operator;
import com.swingchat.repository.query.Sort;
import com.swingchat.repository.query.Statement;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public List<User> findMany(Query query) {
        Statement statement = new Statement("user");
        if (!StringUtils.isBlank(query.getId())) {
            statement.addOperator(new Operator.Eq("id", query.getId()));
        }
        if (!StringUtils.isBlank(query.getName())) {
            statement.addOperator(new Operator.Eq("name", query.getName()));
        }
        if (!StringUtils.isBlank(query.getUsername())) {
            statement.addOperator(new Operator.Eq("username", query.getUsername()));
        }
        if (!StringUtils.isBlank(query.getPassword())) {
            statement.addOperator(new Operator.Eq("password", query.getPassword()));
        }
        if (query.page < 0) {
            statement.page(0);
        }
        if (query.limit < 0) {
            statement.limit(10);
        }
        String sql = statement.build();
        List<User> users = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = User.builder()
                        .id(rs.getString("id"))
                        .name(rs.getString("name"))
                        .username(rs.getString("username"))
                        .password(rs.getString("password"))
                        .build();
                users.add(user);
            }
            return users;

        } catch (SQLException e) {
            log.warning(e.getMessage());
        }
        return List.of();
    }

    @Builder
    @Getter
    public static class Query {
        private String id;
        private String name;
        private String username;
        private String password;
        private int page;
        private int limit;
        private final List<Sort> sorts;
    }

}
