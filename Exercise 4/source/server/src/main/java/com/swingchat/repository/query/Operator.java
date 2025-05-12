package com.swingchat.repository.query;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class Operator {

    protected abstract String toStatement();

    @AllArgsConstructor
    public static class Eq extends Operator {
        private String field;
        private Object value;

        @Override
        protected String toStatement() {
            return field + " = " + value.toString();
        }
    }

    @AllArgsConstructor
    public static class Like extends Operator {
        private String field;
        private String regex;

        @Override
        protected String toStatement() {
            return field + " LIKE " + Pattern.quote(regex);
        }
    }

    @AllArgsConstructor
    public static class Ne extends Operator {
        private String field;
        private Object value;

        @Override
        protected String toStatement() {
            return field + " != " + value.toString();
        }
    }

    @AllArgsConstructor
    public static class Gt extends Operator {
        private String field;
        private Object value;

        @Override
        protected String toStatement() {
            return field + " > " + value.toString();
        }
    }

    @AllArgsConstructor
    public static class Lt extends Operator {
        private String field;
        private Object value;

        @Override
        protected String toStatement() {
            return field + " < " + value.toString();
        }
    }

    @AllArgsConstructor
    public static class Gte extends Operator {
        private String field;
        private Object value;

        @Override
        protected String toStatement() {
            return field + " >= " + value.toString();
        }
    }

    @AllArgsConstructor
    public static class Lte extends Operator {
        private String field;
        private Object value;

        @Override
        protected String toStatement() {
            return field + " <= " + value.toString();
        }
    }

    @AllArgsConstructor
    public static class In extends Operator {
        private String field;
        private List<Object> values;

        @Override
        protected String toStatement() {
            String valueString = values.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", ", "(", ")"));
            return field + " IN " + valueString;
        }
    }

    @AllArgsConstructor
    public static class Nin extends Operator {
        private String field;
        private List<Object> values;

        @Override
        protected String toStatement() {
            String valueString = values.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", ", "(", ")"));
            return field + " NOT IN " + valueString;
        }
    }

    @AllArgsConstructor
    public static class Or extends Operator {
        private List<Operator> operators;

        @Override
        protected String toStatement() {
            String orStatement = operators.stream()
                    .map(Operator::toStatement)
                    .collect(Collectors.joining(" OR "));
            return "(" + orStatement + ")";
        }
    }
}
