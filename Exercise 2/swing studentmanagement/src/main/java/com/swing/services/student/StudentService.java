package com.swing.services.student;

import com.swing.dtos.student.*;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public interface StudentService {
    public void create(CreateStudentRequest request) throws Exception;
    public StudentResponse findOne(FilterStudentsRequest request) throws Exception;
    public void updateOne(long id, UpdateStudentRequest request) throws Exception;
    public void delete(long id) throws SQLException;
    public StudentListResponse findMany(FilterStudentsRequest request) throws SQLException;
    public Boolean existsById(long id) throws SQLException;
    public boolean exportToCSV(List<Long> studentId, File csvFile);
    public boolean importFromCSV(File csvFile) throws  SQLException;
}
