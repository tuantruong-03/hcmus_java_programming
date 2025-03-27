package com.swing.services.student;

import com.swing.dtos.student.CreateStudentRequest;
import com.swing.dtos.student.FilterStudentsRequest;
import com.swing.dtos.student.StudentListResponse;
import com.swing.dtos.student.UpdateStudentRequest;
import com.swing.repository.student.Student;

import java.sql.SQLException;
import java.util.List;

public interface StudentService {
    void create(CreateStudentRequest request) throws Exception;

    void update(long id, UpdateStudentRequest request) throws Exception;

    void delete(long id) throws SQLException;

    StudentListResponse findMany(FilterStudentsRequest request) throws SQLException;

    Boolean existsById(long id) throws SQLException;
}
