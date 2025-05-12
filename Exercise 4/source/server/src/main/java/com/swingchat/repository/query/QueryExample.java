package com.swingchat.repository.query;

import lombok.extern.java.Log;

import java.util.List;

@Log
public class QueryExample {

    public static void main(String[] args) {
        // Create a query for the "users" table
        Statement statement = new Statement("users")
                .addOperator(new Operator.Eq("username", "john"))
                .addOperator(new Operator.Gt("age", 18))
                .addOperator(new Operator.Lt("age", 60))
                .addOperator(new Operator.Gte("score", 50))
                .addOperator(new Operator.Lte("height", 180))
                .addOperator(new Operator.Ne("status", "inactive"))
                .addOperator(new Operator.In("role", List.of("admin", "user", "guest")))
                .addOperator(new Operator.Nin("country", List.of("North Korea", "Iran")))
                .addOperator(new Operator.Like("name", "%doe%")) // starts with "doe"
                .addOperator(new Operator.Or(List.of(
                        new Operator.Eq("country", "USA"),
                        new Operator.Eq("country", "Canada")
                )))
                .addSort(Sort.builder().field("created_at").isAscending(false).build())
                .page(3)
                .limit(20);

        // Print the generated SQL-like statement
       log.info(statement.build());
    }
}
