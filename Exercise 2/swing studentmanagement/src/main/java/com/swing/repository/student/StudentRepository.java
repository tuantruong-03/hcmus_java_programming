package com.swing.repository.student;

import java.sql.SQLException;
import java.util.List;

public interface StudentRepository {
    public void create(Student student) throws SQLException;

    public int update(long id, Student.UpdatePayload request) throws SQLException;

    public int deleteById(long id) throws SQLException;

    List<Student> findMany(StudentQuery query) throws SQLException;

    Boolean existsById(long id) throws SQLException;
}
