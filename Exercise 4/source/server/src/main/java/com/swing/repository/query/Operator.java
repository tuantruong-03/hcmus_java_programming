package com.swing.repository.query;

import java.util.Collection;
import java.util.Collections;
import lombok.Getter;

@Getter
public abstract class Operator {
    protected String column;
    protected Object value;

    protected Operator(String column, Object value) {
        this.column = column;
        this.value = value;
    }

    protected abstract String prepareStatement();

    // Equals ( = )
    public static class Eq extends Operator {
        public Eq(String column, Object value) {
            super(column, value);
        }
        @Override
        protected String prepareStatement() {
            return column + " = ?";
        }
    }

    // Not Equals ( != )
    public static class Neq extends Operator {
        public Neq(String column, Object value) {
            super(column, value);
        }
        @Override
        protected String prepareStatement() {
            return column + " != ?";
        }
    }

    // Greater Than ( > )
    public static class Gt extends Operator {
        public Gt(String column, Object value) {
            super(column, value);
        }
        @Override
        protected String prepareStatement() {
            return column + " > ?";
        }
    }

    // Greater Than or Equal ( >= )
    public static class Gte extends Operator {
        public Gte(String column, Object value) {
            super(column, value);
        }
        @Override
        protected String prepareStatement() {
            return column + " >= ?";
        }
    }

    // Less Than ( < )
    public static class Lt extends Operator {
        public Lt(String column, Object value) {
            super(column, value);
        }
        @Override
        protected String prepareStatement() {
            return column + " < ?";
        }
    }

    public static class Lte extends Operator {
        public Lte(String column, Object value) {
            super(column, value);
        }
        @Override
        protected String prepareStatement() {
            return column + " <= ?";
        }
    }

    public static class Like extends Operator {
        public Like(String column, String value) {
            super(column, value);
        }
        @Override
        protected String prepareStatement() {
            return column + " LIKE ?";
        }
    }

    public static class In extends Operator {
        public In(String column, Collection<?> value) {
            super(column, value);
        }
        @Override
        protected String prepareStatement() {
            int size = ((Collection<?>) value).size();
            String placeholders = String.join(", ",
                    Collections.nCopies(size, "?"));
            return column + " IN (" + placeholders + ")";
        }
    }

    // NOT IN (collection of values)
    public static class NotIn extends Operator {
        public NotIn(String column, Collection<?> value) {
            super(column, value);
        }
        @Override
        protected String prepareStatement() {
            int size = ((Collection<?>) value).size();
            String placeholders = String.join(", ",
                    Collections.nCopies(size, "?"));
            return column + " NOT IN (" + placeholders + ")";
        }
    }
}

