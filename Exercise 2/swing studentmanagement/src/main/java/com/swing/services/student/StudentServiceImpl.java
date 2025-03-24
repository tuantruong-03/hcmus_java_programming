package com.swing.services.student;

import com.swing.dtos.student.CreateStudentRequest;
import com.swing.dtos.student.FilterStudentsRequest;
import com.swing.dtos.student.UpdateStudentRequest;
import com.swing.exceptions.DeleteResourceException;
import com.swing.exceptions.InvalidInputsException;
import com.swing.exceptions.UpdateResourceException;
import com.swing.models.Student;
import com.swing.repository.pagination.Pagination;
import com.swing.repository.pagination.Sort;
import com.swing.repository.student.StudentQuery;
import com.swing.repository.student.StudentRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
//    private final Logger logger = Logger.getLogger(StudentService.class.getName());

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    @Override
    public void create(CreateStudentRequest request) throws Exception  {
        InvalidInputsException invalidInputsException = request.validate();
        if (invalidInputsException != null) {
            throw invalidInputsException;
        }
        Student student = Student.builder()
                .name(request.getName())
                .score(request.getScore())
                .image(request.getImage())
                .note(request.getNote())
                .address(request.getAddress())
                .build();
        studentRepository.create(student);

    }

    @Override
    public void update(long id, UpdateStudentRequest request) throws Exception {
        InvalidInputsException invalidInputsException = request.validate();
        if (invalidInputsException != null) {
            throw invalidInputsException;
        }
        Student.UpdatePayload payload = Student.UpdatePayload.builder()
                .name(request.getName())
                .score(request.getScore())
                .address(request.getAddress())
                .image(request.getImage())
                .note(request.getNote())
                .build();
        int result = studentRepository.update(id, payload);
        if (result <= 0) {
            throw new UpdateResourceException("Could not update student with id: " + id);
        }
    }

    @Override
    public void delete(long id) throws SQLException {
        int result = studentRepository.deleteById(id);
        if (result <= 0) {
            throw new DeleteResourceException("Could not delete student with id: " + id);
        }
    }

    @Override
    public List<Student> findMany(FilterStudentsRequest request) throws SQLException {
        Sort sort = Sort.builder()
                .field(request.getSortField())
                .ascending(Objects.equals(request.getSortOrder(), "ASC"))
                .build();
        StudentQuery query = StudentQuery.builder()
                .search(request.getSearch())
                .pagination(Pagination.builder().page(request.getPage()).size(request.getSize()).sort(sort).build())
                .filter(null)
                .build();
        return studentRepository.findMany(query);
    }
    @Override
    public Boolean existsById(long id) throws SQLException {
       return studentRepository.existsById(id);
    }
}
