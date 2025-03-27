package com.swing.services.student;

import com.swing.dtos.student.*;

import java.sql.SQLException;

public interface StudentService {
    void create(CreateStudentRequest request) throws Exception;

    StudentResponse findOne(FilterStudentsRequest request) throws Exception;

    void updateOne(long id, UpdateStudentRequest request) throws Exception;

    void delete(long id) throws SQLException;

    StudentListResponse findMany(FilterStudentsRequest request) throws SQLException;

    Boolean existsById(long id) throws SQLException;
}
