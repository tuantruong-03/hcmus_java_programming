package com.swing.repository.student;

import java.sql.SQLException;
import java.util.List;

public interface StudentRepository {
    public void create(Student student) throws SQLException;
    public void createMany(List<Student> students) throws SQLException;
    public int update(long id, Student.UpdatePayload request) throws SQLException;
    public int deleteById(long id) throws SQLException;
    public List<Student> findMany(StudentQuery query) throws SQLException;
    public Student findOne(StudentQuery query) throws SQLException;
    public boolean existsById(long id) throws SQLException;

}
