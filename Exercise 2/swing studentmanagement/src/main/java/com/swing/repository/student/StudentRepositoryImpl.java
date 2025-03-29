package com.swing.repository.student;

import com.swing.config.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class StudentRepositoryImpl implements StudentRepository {
    private final Database db;
    private final Logger log = Logger.getLogger(StudentRepositoryImpl.class.getName());

    public StudentRepositoryImpl(Database db) {
        this.db = db;
    }

    @Override
    public void create(Student student) throws SQLException {
        boolean hasId = student.getId() != null;
        String sql = hasId
                ? "INSERT INTO students (id, name, score, image, address, note) VALUES (?,?,?,?,?,?)"
                : "INSERT INTO students (name, score, image, address, note) VALUES (?,?,?,?,?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int index = 1;
            if (hasId) {
                ps.setLong(index++, student.getId());
            }
            ps.setString(index++, student.getName());
            ps.setDouble(index++, student.getScore());
            ps.setString(index++, student.getImage());
            ps.setString(index++, student.getAddress());
            ps.setString(index, student.getNote());

            ps.executeUpdate();
            log.info("Student added successfully");
        }
    }

    public void createMany(List<Student> students) throws SQLException {
        if (students == null || students.isEmpty()) {
            return;  // Nothing to insert
        }

        boolean hasId = students.get(0).getId() != null;  // Check if ID is provided

        String sql = hasId
                ? "INSERT INTO students (id, name, score, image, address, note) VALUES (?,?,?,?,?,?)"
                : "INSERT INTO students (name, score, image, address, note) VALUES (?,?,?,?,?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Student student : students) {
                int index = 1;
                if (hasId) {
                    ps.setLong(index++, student.getId());
                }
                ps.setString(index++, student.getName());
                ps.setDouble(index++, student.getScore());
                ps.setString(index++, student.getImage());
                ps.setString(index++, student.getAddress());
                ps.setString(index, student.getNote());

                ps.addBatch();  // Add to batch
            }

            ps.executeBatch();  // Execute batch insert
            log.info(students.size() + " students added successfully");
        }
    }


    @Override
    public int update(long id, Student.UpdatePayload payload) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE students SET");
        boolean first = true;
        if (payload.getName() != null) {
            sql.append(" name = ?");
            first = false;
        }
        if (payload.getScore() != null) {
            if (!first) sql.append(" ,");
            sql.append(" score = ?");
            first = false;
        }
        if (payload.getImage() != null) {
            if (!first) sql.append(" ,");
            sql.append(" image = ?");
            first = false;
        }
        if (payload.getAddress() != null) {
            if (!first) sql.append(" ,");
            sql.append(" address = ?");
            first = false;
        }
        if (payload.getNote() != null) {
            if (!first) sql.append(" ,");
            sql.append(" note = ?");
        }
        sql.append(" WHERE id = ?");

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            if (payload.getName() != null) ps.setString(index++, payload.getName());
            if (payload.getScore() != null) ps.setDouble(index++, payload.getScore());
            if (payload.getImage() != null) ps.setString(index++, payload.getImage());
            if (payload.getAddress() != null) ps.setString(index++, payload.getAddress());
            if (payload.getNote() != null) ps.setString(index++, payload.getNote());
            ps.setLong(index, id);
            int result = ps.executeUpdate();
            log.info("Student updated successfully");
            return result;
        }
    }

    @Override
    public int deleteById(long id) throws SQLException {
        String sql = "DELETE FROM students WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            int result = ps.executeUpdate();
            log.info("Student deleted successfully!");
            return result;
        }
    }

    @Override
    public List<Student> findMany(StudentQuery query) throws SQLException {
        List<Student> students = new ArrayList<>();
        try (Connection conn = db.getConnection();  // Get connection once
             PreparedStatement ps = prepareQueryStatement(conn, query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Student student = Student.builder()
                        .id(rs.getLong(Student.ColumnId))
                        .name(rs.getString(Student.ColumnName))
                        .score(rs.getDouble(Student.ColumnScore))
                        .image(rs.getString(Student.ColumnImage))
                        .address(rs.getString(Student.ColumnAddress))
                        .note(rs.getString(Student.ColumnNote))
                        .build();
                students.add(student);
            }
        }
        return students; // Connection closes automatically after this
    }


    @Override
    public Student findOne(StudentQuery query) throws SQLException {
        try (Connection conn = db.getConnection();  // Get connection once
             PreparedStatement ps = prepareQueryStatement(conn, query)) {
            ps.setMaxRows(1);  // Limit to 1 row result
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Student.builder()
                            .id(rs.getLong(Student.ColumnId))
                            .name(rs.getString(Student.ColumnName))
                            .score(rs.getDouble(Student.ColumnScore))
                            .image(rs.getString(Student.ColumnImage))
                            .address(rs.getString(Student.ColumnAddress))
                            .note(rs.getString(Student.ColumnNote))
                            .build();
                }
            }
        }
        return null;  // If no student is found, return null
    }

    public PreparedStatement prepareQueryStatement(Connection connection, StudentQuery query) throws SQLException {
        if (query == null) {
            query = StudentQuery.defaultInstance();
        }
        StudentQuery.Filter filter = query.getFilter();
        StringBuilder sql = new StringBuilder("SELECT * FROM students WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (filter != null) {
            if (filter.getId() != null) {
                sql.append(" AND id = ?");
                params.add(filter.getId());
            }
            if (filter.getName() != null) {
                sql.append(" AND name = ?");
                params.add(filter.getName());
            }
            if (filter.getInIds() != null && filter.getInIds().isEmpty()) {
                sql.append(" AND id IN (");
                sql.append("?, ".repeat(filter.getInIds().size()));
                sql.setLength(sql.length() - 2);
                sql.append(")");
                params.addAll(filter.getInIds());
            }
        }

        if (query.getSearch() != null && !query.getSearch().isBlank()) {
            sql.append(" AND (id LIKE ? OR name LIKE ? OR address LIKE ?)");
            String searchPattern = "%" + query.getSearch() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        // Sorting
        if (query.getPagination() != null && query.getPagination().getSort() != null) {
            String field = query.getPagination().getSort().getField();
            boolean isValid = validateSortField(field);
            if (!isValid) {
                field = Student.ColumnId;
            }
            sql.append(" ORDER BY ")
                    .append(field)
                    .append(Boolean.TRUE.equals(query.getPagination().getSort().getAscending()) ? " ASC" : " DESC");
        }
        // Pagination
        if (query.getPagination() != null) {
            sql.append(" LIMIT ? OFFSET ?");
            params.add(query.getPagination().getSize());
            params.add(query.getPagination().getPage() * query.getPagination().getSize());
        }

        PreparedStatement ps = connection.prepareStatement(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            if (param instanceof String str) {
                ps.setString(i + 1, str);
            } else if (param instanceof Long longValue) {
                ps.setLong(i + 1, longValue);
            } else if (param instanceof Integer intValue) {
                ps.setInt(i + 1, intValue);
            }
        }
        return ps;

    }

    private boolean validateSortField(String sortField) {
        return sortField != null &&
                (sortField.equals(Student.ColumnId)
                        || sortField.equals(Student.ColumnName)
                        || sortField.equals(Student.ColumnScore)
                        || sortField.equals(Student.ColumnImage)
                        || sortField.equals(Student.ColumnAddress)
                        || sortField.equals(Student.ColumnNote));
    }

    @Override
    public boolean existsById(long id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM students WHERE id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }
}
