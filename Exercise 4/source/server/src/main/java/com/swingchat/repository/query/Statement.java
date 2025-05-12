package com.swingchat.repository.query;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Statement {
    private final String table;
    private final List<Operator> operators;
    private int page;
    private int limit;
    private final List<Sort> sorts;

    public Statement(String table) {
        this.page = 1;
        this.limit = 100;
        this.operators = new ArrayList<>();
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

    public PreparedStatement prepare(Connection connection) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(table);

        if (!operators.isEmpty()) {
            sql.append(" WHERE ");
            String whereClause = operators.stream()
                    .map(Operator::prepareStatement)
                    .collect(Collectors.joining(" AND "));
            sql.append(whereClause);
        }

        if (!sorts.isEmpty()) {
            sql.append(" ORDER BY ");
            String orderByClause = sorts.stream()
                    .map(Sort::prepareStatement)
                    .collect(Collectors.joining(", "));
            sql.append(orderByClause);
        }

        int offset = (page - 1) * limit;
        sql.append(" LIMIT ").append(limit).append(" OFFSET ").append(offset);

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {
            int parameterIndex = 1;
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
    }

}
