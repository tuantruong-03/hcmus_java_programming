package com.swing.repository;

import com.swing.database.Database;
import com.swing.models.ChatRoom;
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
import java.util.List;

@Log
public class ChatRoomRepository {
    private final Database db;

    private static final String TABLE_NAME = "chat_rooms";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_IS_GROUP= "is_group";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_UPDATED_AT = "updated_at";
    public ChatRoomRepository(Database db) {
        this.db = db;
    }
    public Result<Void> createOne(ChatRoom chatRoom) {
        String sql = "INSERT INTO " + TABLE_NAME +" (id, name, is_group, created_at) VALUES (?, ?, ?, ?)";
        int columnIndex = 1;
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(columnIndex++, chatRoom.getId());
            stmt.setString(columnIndex++, chatRoom.getName());
            stmt.setBoolean(columnIndex++, chatRoom.getIsGroup());
            stmt.setDate(columnIndex, new Date(new java.util.Date().getTime()));
            stmt.executeUpdate();
            log.info("chatRoomUser created successfully.");
            return Result.success(null);
        } catch (SQLException e) {
            return Result.failure(e);
        }
    }

    public Result<ChatRoom> findOne(Query query) {
        query.setLimit(1);
        var statement = buildStatement(query);
        if (statement.isFailure()) {
            return Result.failure(statement.getException());
        }
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = statement.getValue().prepareGetQuery(conn);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                ChatRoom chatRoom = ChatRoom.builder()
                        .id(rs.getString(COLUMN_ID))
                        .name(rs.getString(COLUMN_NAME))
                        .isGroup(rs.getBoolean(COLUMN_IS_GROUP))
                        .createdAt(rs.getDate(COLUMN_CREATED_AT))
                        .updatedAt(rs.getDate(COLUMN_UPDATED_AT))
                        .build();
                return Result.success(chatRoom);
            }
            return Result.success(null);
        } catch (SQLException e) {
            return Result.failure(e);
        }
    }

    public Result<List<ChatRoom>> findMany(Query query) {
        var statement = buildStatement(query);
        if (statement.isFailure()) {
            return Result.failure(statement.getException());
        }
        List<ChatRoom> chatRooms = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = statement.getValue().prepareGetQuery(conn);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ChatRoom chatRoom = ChatRoom.builder()
                        .id(rs.getString(COLUMN_ID))
                        .name(rs.getString(COLUMN_NAME))
                        .isGroup(rs.getBoolean(COLUMN_IS_GROUP))
                        .createdAt(rs.getDate(COLUMN_CREATED_AT))
                        .updatedAt(rs.getDate(COLUMN_UPDATED_AT))
                        .build();
                chatRooms.add(chatRoom);
            }
            return Result.success(chatRooms);

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
        var statement = new Statement(TABLE_NAME);
        if (!StringUtils.isBlank(query.getChatRoomId())) {
            statement.addOperator(new Operator.Eq(COLUMN_ID, query.getChatRoomId()));
        }
        if (!StringUtils.isBlank(query.getName())) {
            statement.addOperator(new Operator.Eq(COLUMN_NAME, query.getName()));
        }
        if (query.isGroup != null) {
            statement.addOperator(new Operator.Eq(COLUMN_IS_GROUP, query.isGroup));
        }
        if (query.getInChatRoomIds() != null && !query.getInChatRoomIds().isEmpty()) {
            statement.addOperator(new Operator.In(COLUMN_ID, query.getInChatRoomIds()));
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
        private String name;
        private List<String> inChatRoomIds;
        private Boolean isGroup;
        private int page;
        @Setter
        private int limit;
        private final List<Sort> sorts;

    }
}
