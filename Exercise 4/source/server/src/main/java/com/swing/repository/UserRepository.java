package com.swing.repository;

import com.swing.database.Database;
import com.swing.models.User;
import com.swing.repository.query.Operator;
import com.swing.repository.query.Sort;
import com.swing.repository.query.Statement;
import com.swing.types.Result;
import com.swing.utils.HashUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
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
    public Result<Void> createOne(User user) {
        String sql = "INSERT INTO user (id, name, username, password) VALUES (?, ?, ?, ?)";
        int columnIndex = 1;
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(columnIndex++, user.getId());
            stmt.setString(columnIndex++, user.getName());
            stmt.setString(columnIndex++, user.getUsername());
            var hashedPassword = HashUtils.sha256HashToHexString(user.getPassword());
            if (hashedPassword.isFailure()) {
                return Result.failure(hashedPassword.getException());
            }
            stmt.setString(columnIndex, hashedPassword.getValue());
            stmt.executeUpdate();
            log.info("User created successfully.");
            return Result.success(null);
        } catch (SQLException e) {
            return Result.failure(e);
        }
    }

    public Result<User> findOne(Query query) {
        query.setLimit(1);
        var statement = buildStatement(query);
        if (statement.isFailure()) {
            return Result.failure(statement.getException());
        }
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = statement.getValue().prepareGetQuery(conn);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                User user = User.builder()
                        .id(rs.getString("id"))
                        .name(rs.getString("name"))
                        .username(rs.getString("username"))
                        .password(rs.getString("password"))
                        .build();
                return Result.success(user);
            }
            return Result.success(null);
        } catch (SQLException e) {
            return Result.failure(e);
        }
    }

    public Result<List<User>> findMany(Query query) {
        var statement = buildStatement(query);
        if (statement.isFailure()) {
            return Result.failure(statement.getException());
        }
        List<User> users = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = statement.getValue().prepareGetQuery(conn);
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
            return Result.success(users);

        } catch (SQLException e) {
            return Result.failure(e);
        }
    }

    public Result<Boolean> doesExist(Query query) {
        var result = count(query);
        if (result.isFailure()) {
            return Result.failure(result.getException());
        }
        return result.getValue() >= 1 ? Result.success(Boolean.TRUE) : Result.success(Boolean.FALSE);
    }

    public Result<Integer> count(Query query) {
        var statement = buildStatement(query);
        if (statement.isFailure()) {
            return Result.failure(statement.getException());
        }
        int count = 0;
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = statement.getValue().prepareCountQuery(conn);
             ResultSet rs = stmt.executeQuery()) {
             if (rs.next()) {
                 count = rs.getInt(1);
             }
             return Result.success(count);

        } catch (SQLException e) {
            return Result.failure(e);
        }
    }

    private Result<Statement> buildStatement(Query query) {
        var statement = new Statement("user");
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
            var hashedPassword = HashUtils.sha256HashToHexString(query.getPassword());
            if (hashedPassword.isFailure()) {
                return Result.failure(hashedPassword.getException());
            }
            statement.addOperator(new Operator.Eq("password", hashedPassword));
        }
        if (query.page < 0) {
            statement.page(0);
        }
        if (query.limit < 0) {
            statement.limit(10);
        }
        return Result.success(statement);
    }

    @Builder
    @Getter
    public static class Query {
        private String id;
        private String name;
        private String username;
        private String password;
        private int page;
        @Setter
        private int limit;
        private final List<Sort> sorts;

    }
}
