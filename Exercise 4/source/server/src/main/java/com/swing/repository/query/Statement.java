package com.swing.repository.query;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Statement {
    private final String table;
    private final List<Operator> operators;
    private final List<Map.Entry<String, Object>> columnsToUpdate;
    private int page;
    private int limit;
    private final List<Sort> sorts;

    public Statement(String table) {
        this.page = 1;
        this.limit = 100;
        this.operators = new ArrayList<>();
        this.columnsToUpdate = new ArrayList<>();
        this.sorts = new ArrayList<>();
        this.table = table;
    }

    public Statement addOperator(Operator operator) {
        this.operators.add(operator);
        return this;
    }

    public Statement addSort(Sort sort) {
        this.sorts.add(sort);
        return this;
    }

    public Statement page(int page) {
        this.page = page;
        return this;
    }

    public Statement limit(int limit) {
        this.limit = limit;
        return this;
    }

    public Statement setColumn(String column, Object value) {
        this.columnsToUpdate.add(new AbstractMap.SimpleEntry<>(column, value));
        return this;
    }

    public PreparedStatement prepareGetQuery(Connection connection) throws SQLException {
        String sql = "SELECT * FROM " + table + prepareConditions(true);
        return prepare(connection, sql);
    }

    public PreparedStatement prepareCountQuery(Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + table + prepareConditions(false);
        return prepare(connection, sql);
    }

    public PreparedStatement prepareUpdateQuery(Connection connection) throws SQLException {
        if (columnsToUpdate.isEmpty()) {
            throw new SQLException("No columns specified for update.");
        }
        StringBuilder sql = new StringBuilder("UPDATE ").append(table).append(" SET ");
        String setClause = columnsToUpdate.stream()
                .map(column -> column + " = ?")
                .collect(Collectors.joining(", "));
        sql.append(setClause);
        sql.append(prepareConditions(false));
        return prepare(connection, sql.toString());
    }

    public PreparedStatement prepareDeleteQuery(Connection connection) throws SQLException {
        String sql = "DELETE FROM " + table + prepareConditions(false);
        return prepare(connection, sql);
    }

    private PreparedStatement prepare(Connection connection, String sql) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        int parameterIndex = 1;
        for (Map.Entry<String, Object> ctu : columnsToUpdate) {
            preparedStatement.setObject(parameterIndex++, ctu.getKey());
        }
        for (Operator operator : operators) {
            Object value = operator.getValue();
            if (value instanceof Iterable) {
                for (Object val : (Collection<?>) value) {
                    preparedStatement.setObject(parameterIndex++, val);
                }
            } else {
                preparedStatement.setObject(parameterIndex++, value);
            }
        }
        return preparedStatement;
    }

    public StringBuilder prepareConditions(boolean pagination) {
        StringBuilder conditions = new StringBuilder();
        if (!operators.isEmpty()) {
            conditions.append(" WHERE ");
            String whereClause = operators.stream()
                    .map(Operator::prepareStatement)
                    .collect(Collectors.joining(" AND "));
            conditions.append(whereClause);
        }
        if (pagination) {
            if (!sorts.isEmpty()) {
                conditions.append(" ORDER BY ");
                String orderByClause = sorts.stream()
                        .map(Sort::prepareStatement)
                        .collect(Collectors.joining(", "));
                conditions.append(orderByClause);
            }

            int offset = (page - 1) * limit;
            conditions.append(" LIMIT ").append(limit).append(" OFFSET ").append(offset);
        }
        return conditions;
    }
}
