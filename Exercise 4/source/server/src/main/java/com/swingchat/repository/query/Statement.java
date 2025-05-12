package com.swingchat.repository.query;


import java.util.ArrayList;
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


    public String build() {
        StringBuilder statement = new StringBuilder("SELECT * FROM " + table);

        // Add WHERE clause if there are any operators
        if (!operators.isEmpty()) {
            String whereClause = operators.stream()
                    .map(Operator::toStatement)
                    .collect(Collectors.joining(" AND "));
            statement.append(" WHERE ").append(whereClause);
        }

        // Add ORDER BY clause if there are any sorts
        if (!sorts.isEmpty()) {
            String orderClause = sorts.stream()
                    .map(Sort::toStatement)
                    .collect(Collectors.joining(", "));
            statement.append(" ORDER BY ").append(orderClause);
        }

        // Add LIMIT and OFFSET for pagination
        int offset = (page - 1) * limit;
        statement.append(" LIMIT ").append(limit).append(" OFFSET ").append(offset);

        return statement.toString();
    }

}
