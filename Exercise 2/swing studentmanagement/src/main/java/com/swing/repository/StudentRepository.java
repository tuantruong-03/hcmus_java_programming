package com.swing.repository;

import com.swing.database.Database;
import com.swing.dtos.student.UpdateStudentRequest;
import com.swing.models.Student;
import com.swing.repository.query.Where;
import com.swing.repository.query.Query;
import lombok.Builder;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class StudentRepository {
    private final Database db;
    private final Logger log = Logger.getLogger(StudentRepository.class.getName());

    public StudentRepository(Database db) {
        this.db = db;
    }

    public void add(Student student) throws SQLException {
        String sql = "INSERT INTO students (name, score, image, address, note) VALUES (?,?,?,?, ?)";
        Connection conn = db.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, student.getName());
        ps.setDouble(2, student.getScore());
        ps.setString(3, student.getImage());
        ps.setString(4, student.getAddress());
        ps.setString(5, student.getNote());
        ps.executeUpdate();
        log.info("Student added successfully");
    }

    public void update(long id, UpdateStudentRequest request) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE students SET");
        boolean first = true;
        if (request.getName() != null) {
            sql.append(" name = ?");
            first = false;
        }
        if (request.getScore() != null) {
            if (!first) sql.append(" ,");
            sql.append(" score = ?");
            first = false;
        }
        if (request.getImage() != null) {
            if (!first) sql.append(" ,");
            sql.append(" image = ?");
            first = false;
        }
        if (request.getAddress() != null) {
            if (!first) sql.append(" ,");
            sql.append(" address = ?");
            first = false;
        }
        if (request.getNote() != null) {
            if (!first) sql.append(" ,");
            sql.append(" note = ?");
        }
        sql.append(" WHERE id = ?");
        Connection conn = db.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql.toString());
        int index = 1;
        if (request.getName() != null) ps.setString(index++, request.getName());
        if (request.getScore() != null) ps.setDouble(index++, request.getScore());
        if (request.getImage() != null) ps.setString(index++, request.getImage());
        if (request.getAddress() != null) ps.setString(index++, request.getAddress());
        if (request.getNote() != null) ps.setString(index++, request.getNote());
        ps.setLong(index, id);
        ps.executeUpdate();
        log.info("Student updated successfully");

    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM students WHERE id=?";
        Connection conn = db.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, id);
        ps.executeUpdate();
        log.info("Student deleted successfully!");
    }

    public List<Student> findMany(Query query) throws SQLException {
        if (query == null) {
            query = Query.defaultInstance();
        }
        Filter filter = (Filter) query.getFilter();
        StringBuilder sql = new StringBuilder("SELECT * FROM students WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (filter != null) {
            if (filter.getId() != null) {
                sql.append(" AND id = ?");
                params.add("%" + filter.getId() + "%");
            }
            if (filter.getName() != null) {
                sql.append(" AND name LIKE ?");
                params.add("%" + filter.getName() + "%");
            }
        }

        // Sorting
        if (query.getPagination() != null && query.getPagination().getSort() != null) {
            String field = query.getPagination().getSort().getField();
            boolean isValidField = field != null &&
                    (field.equals(Student.ColumnId)
                            || field.equals(Student.ColumnName)
                    || field.equals(Student.ColumnScore)
                    || field.equals(Student.ColumnImage)
                    || field.equals(Student.ColumnAddress)
                    || field.equals(Student.ColumnNote));
            if (!isValidField) {
                field = Student.ColumnId;
            }
            sql.append(" ORDER BY ")
                    .append(field)
                    .append(query.getPagination().getSort().getAscending() ? " ASC" : " DESC");
        }
        // Pagination
        if (query.getPagination() != null) {
            sql.append(" LIMIT ? OFFSET ?");
            params.add(query.getPagination().getSize());
            params.add(query.getPagination().getPage() * query.getPagination().getSize());
        }
        List<Student> students = new ArrayList<>();
        Connection conn = db.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql.toString());
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

        ResultSet rs = ps.executeQuery();
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
        return students;
    }



    @Builder
    @Getter
    public static class Filter extends Where {
        private Long id;
        private String name;
    }
}
