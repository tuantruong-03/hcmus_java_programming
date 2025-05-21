package com.swing.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swing.database.Database;
import com.swing.models.Message;
import com.swing.repository.query.Operator;
import com.swing.repository.query.Sort;
import com.swing.repository.query.Statement;
import com.swing.types.Result;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Log
public class MessageRepository {
    private final Database db;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TABLE_NAME = "messages";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CHAT_ROOM_ID = "chat_room_id";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_SENDER_ID = "sender_id";
    private static final String COLUMN_SENDER_AVATAR = "sender_avatar";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_UPDATED_AT = "updated_at";

    public MessageRepository(Database db) {
        this.db = db;
    }

    public Result<Void> createOne(Message message) {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, chat_room_id, content, sender_id, sender_avatar, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int columnIndex = 1;
            stmt.setString(columnIndex++, message.getId());
            stmt.setString(columnIndex++, message.getChatRoomId());
            String contentJson = objectMapper.writeValueAsString(message.getContent());
            stmt.setString(columnIndex++, contentJson);
            stmt.setString(columnIndex++, message.getSenderId());
            stmt.setString(columnIndex++, message.getSenderAvatar());
            stmt.setDate(columnIndex, new Date(new java.util.Date().getTime()));
            stmt.executeUpdate();
            log.info("Message created successfully: " + message.getId());
            return Result.success(null);
        } catch (Exception e) {
            log.warning("Error creating message: " + e.getMessage());
            return Result.failure(e);
        }
    }

    public Result<Message> findOne(Query query) {
        query.setLimit(1);
        var statementResult = buildStatement(query);
        if (statementResult.isFailure()) {
            return Result.failure(statementResult.getException());
        }

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = statementResult.getValue().prepareGetQuery(conn);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return Result.success(mapRow(rs));
            }
            return Result.success(null);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    public Result<Boolean> delete(Query query) {
        var statementResult = buildStatement(query);
        if (statementResult.isFailure()) {
            return Result.failure(statementResult.getException());
        }
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = statementResult.getValue().prepareDeleteQuery(conn)) {
            int count = stmt.executeUpdate();
            return count > 0 ? Result.success(Boolean.TRUE) : Result.success(Boolean.FALSE);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    public Result<Boolean> update(UpdatePayload payload, Query query) {
        var statementResult = buildStatement(query);
        if (statementResult.isFailure()) {
            return Result.failure(statementResult.getException());
        }
        var statement = statementResult.getValue();
        boolean isUpdated = false;
        if (payload.content != null) {
            isUpdated = true;
            statement.setColumn(COLUMN_CONTENT, payload.getContent());
        }
        if (!isUpdated) {
            return Result.failure(new Exception("update payload contains nothing"));
        }
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = statementResult.getValue().prepareUpdateQuery(conn)) {
            int count = stmt.executeUpdate();
            return count > 0 ? Result.success(Boolean.TRUE) : Result.success(Boolean.FALSE);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    public Result<List<Message>> findMany(Query query) {
        var statementResult = buildStatement(query);
        if (statementResult.isFailure()) {
            return Result.failure(statementResult.getException());
        }

        List<Message> messages = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = statementResult.getValue().prepareGetQuery(conn);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                messages.add(mapRow(rs));
            }
            return Result.success(messages);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    public Result<Boolean> doesExist(Query query) {
        var countResult = count(query);
        if (countResult.isFailure()) {
            return Result.failure(countResult.getException());
        }
        return Result.success(countResult.getValue() >= 1);
    }

    public Result<Integer> count(Query query) {
        var statementResult = buildStatement(query);
        if (statementResult.isFailure()) {
            return Result.failure(statementResult.getException());
        }

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = statementResult.getValue().prepareCountQuery(conn);
             ResultSet rs = stmt.executeQuery()) {

            int count = rs.next() ? rs.getInt(1) : 0;
            return Result.success(count);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    private Message mapRow(ResultSet rs) throws Exception {
        return Message.builder()
                .id(rs.getString(COLUMN_ID))
                .chatRoomId(rs.getString(COLUMN_CHAT_ROOM_ID))
                .senderId(rs.getString(COLUMN_SENDER_ID))
                .senderAvatar(rs.getString(COLUMN_SENDER_AVATAR))
                .createdAt(rs.getTimestamp(COLUMN_CREATED_AT))
                .updatedAt(rs.getTimestamp(COLUMN_UPDATED_AT))
                .content(objectMapper.readValue(rs.getString(COLUMN_CONTENT), Message.Content.class))
                .build();
    }

    private Result<Statement> buildStatement(Query query) {
        var statement = new Statement(TABLE_NAME);

        if (query.getId() != null) {
            statement.addOperator(new Operator.Eq(COLUMN_ID, query.getId()));
        }
        if (query.getChatRoomId() != null) {
            statement.addOperator(new Operator.Eq(COLUMN_CHAT_ROOM_ID, query.getChatRoomId()));
        }
        if (query.getSenderId() != null) {
            statement.addOperator(new Operator.Eq(COLUMN_SENDER_ID, query.getSenderId()));
        }
        if (query.getInIds() != null && !query.getInIds().isEmpty()) {
            statement.addOperator(new Operator.In(COLUMN_ID, query.getInIds()));
        }

        if (query.page < 0) statement.page(0);
        if (query.limit < 0) statement.limit(10);

        return Result.success(statement);
    }

    @Builder
    @Getter
    public static class Query {
        private String id;
        private String chatRoomId;
        private String senderId;
        private List<String> inIds;
        private int page;
        @Setter
        private int limit;
        private List<Sort> sorts;
    }

    @Builder
    @Getter
    public static class UpdatePayload {
        private Message.Content content;
    }
}
