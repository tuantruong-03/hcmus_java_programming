package com.swing.repository;

import com.swing.database.Database;
import com.swing.models.ChatRoomUser;
import com.swing.repository.query.Operator;
import com.swing.repository.query.Sort;
import com.swing.repository.query.Statement;
import com.swing.types.Result;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log
public class ChatRoomUserRepository {
    private final Database db;

    private static final String TABLE_NAME = "chatroom_user";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_CHATROOM_ID = "chat_room_id";

    public ChatRoomUserRepository(Database db) {
        this.db = db;
    }

    public Result<Void> createOne(ChatRoomUser chatRoomUser) {
        String sql = "INSERT INTO " + TABLE_NAME + " (chatroom_id, user_id) VALUES (?, ?)";
        int columnIndex = 1;
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(columnIndex++, chatRoomUser.getChatRoomId());
            stmt.setString(columnIndex, chatRoomUser.getUserId());
            stmt.executeUpdate();
            log.info("chatRoomUser created successfully.");
            return Result.success(null);
        } catch (SQLException e) {
            return Result.failure(e);
        }
    }

    public Result<Void> createMany(List<ChatRoomUser> chatRoomUsers) {
        if (chatRoomUsers == null || chatRoomUsers.isEmpty()) {
            return Result.success(null); // No-op for empty input
        }
        String sql = "INSERT INTO chatroom_user (chat_room_id, user_id) VALUES (?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (ChatRoomUser user : chatRoomUsers) {
                int columnIndex = 1;
                stmt.setString(columnIndex++, user.getChatRoomId());
                stmt.setString(columnIndex, user.getUserId());
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit(); // Commit transaction
            return Result.success(null);
        } catch (SQLException e) {
            return Result.failure(e);
        }
    }


    public Result<ChatRoomUser> findOne(Query query) {
        query.setLimit(1);
        var statement = buildStatement(query);
        if (statement.isFailure()) {
            return Result.failure(statement.getException());
        }
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = statement.getValue().prepareGetQuery(conn);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                ChatRoomUser chatRoomUser = ChatRoomUser.builder()
                        .chatRoomId(rs.getString(COLUMN_CHATROOM_ID))
                        .userId(rs.getString(COLUMN_USER_ID))
                        .build();
                return Result.success(chatRoomUser);
            }
            return Result.success(null);
        } catch (SQLException e) {
            return Result.failure(e);
        }
    }

    public Result<List<String>> findChatRoomIdsByUserIds(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        String inClause = userIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));

        String sql = "SELECT chat_room_id FROM chatroom_user " +
                "WHERE user_id IN (" + inClause + ") " +
                "GROUP BY chat_room_id " +
                "HAVING COUNT(DISTINCT user_id) = ?";

        List<String> result = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int index = 1;
            for (String userId : userIds) {
                stmt.setString(index++, userId);
            }
            stmt.setInt(index, userIds.size());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getString(COLUMN_CHATROOM_ID));
                }
            }

        } catch (SQLException e) {
            return Result.failure(e);
        }

        return Result.success(result);
    }


    public Result<List<ChatRoomUser>> findMany(Query query) {
        var statement = buildStatement(query);
        if (statement.isFailure()) {
            return Result.failure(statement.getException());
        }
        List<ChatRoomUser> chatRoomUsers = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = statement.getValue().prepareGetQuery(conn);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ChatRoomUser chatRoomUser = ChatRoomUser.builder()
                        .chatRoomId(rs.getString(COLUMN_CHATROOM_ID))
                        .userId(rs.getString(COLUMN_USER_ID))
                        .build();
                chatRoomUsers.add(chatRoomUser);
            }
            return Result.success(chatRoomUsers);

        } catch (SQLException e) {
            return Result.failure(e);
        }
    }

    public Result<Boolean> existBy(Query query) {
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
        var statement = new Statement(TABLE_NAME);
        if (!StringUtils.isBlank(query.getChatRoomId())) {
            statement.addOperator(new Operator.Eq(COLUMN_CHATROOM_ID, query.getChatRoomId()));
        }
        if (!StringUtils.isBlank(query.getUserId())) {
            statement.addOperator(new Operator.Eq(COLUMN_USER_ID, query.getUserId()));
        }
        if (query.getInChatRoomIds() != null && !query.getInChatRoomIds().isEmpty()) {
            statement.addOperator(new Operator.In(COLUMN_CHATROOM_ID, query.getInChatRoomIds()));
        }
        if (query.getInUserIds() != null && !query.getInUserIds().isEmpty()) {
            statement.addOperator(new Operator.In(COLUMN_USER_ID, query.getInUserIds()));
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
        private String chatRoomId;
        private String userId;
        private List<String> inChatRoomIds;
        private List<String> inUserIds;
        private int page;
        @Setter
        private int limit;
        private final List<Sort> sorts;

    }
}
