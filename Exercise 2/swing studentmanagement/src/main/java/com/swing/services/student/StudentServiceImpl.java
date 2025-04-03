package com.swing.services.student;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.swing.dtos.student.*;
import com.swing.exceptions.DeleteResourceException;
import com.swing.exceptions.InvalidInputsException;
import com.swing.exceptions.UpdateResourceException;
import com.swing.repository.student.Student;
import com.swing.repository.pagination.Pagination;
import com.swing.repository.pagination.Sort;
import com.swing.repository.student.StudentQuery;
import com.swing.repository.student.StudentRepository;


import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final Logger logger = Logger.getLogger(StudentServiceImpl.class.getName());

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    @Override
    public void create(CreateStudentRequest request) throws Exception {
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
    public StudentListResponse findMany(FilterStudentsRequest request) throws SQLException {
        Sort sort = Sort.builder()
                .field(request.getSortField())
                .ascending(Objects.equals(request.getSortOrder(), "ASC"))
                .build();
        int page = request.getPage() == null ? 0 : request.getPage();
        int size = request.getSize() == null ? 10 : request.getSize();

        StudentQuery query = StudentQuery.builder()
                .search(request.getSearch())
                .pagination(Pagination.builder().page(page).size(size).sort(sort).build())
                .filter(null)
                .build();
        List<Student> students = studentRepository.findMany(query);
        List<StudentResponse> studentResponses = new ArrayList<>();
        students.forEach(student -> {
            StudentResponse response = StudentResponse.builder()
                    .id(student.getId())
                    .name(student.getName())
                    .score(student.getScore())
                    .image(student.getImage())
                    .note(student.getNote())
                    .address(student.getAddress())
                    .build();
            studentResponses.add(response);
        });
        return StudentListResponse.builder()
                .studentResponses(studentResponses)
                .build();
    }

    @Override
    public StudentResponse findOne(FilterStudentsRequest request) throws Exception {
        StudentQuery query = StudentQuery.builder()
                .search(request.getSearch())
                .filter(StudentQuery.Filter.builder().id(request.getId()).build())
                .build();
        Student student = studentRepository.findOne(query);
        if (student == null) {
            return null;
        }
        return StudentResponse.builder()
                .id(student.getId())
                .name(student.getName())
                .score(student.getScore())
                .image(student.getImage())
                .note(student.getNote())
                .address(student.getAddress())
                .build();
    }

    @Override
    public void updateOne(long id, UpdateStudentRequest request) throws Exception {
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
    public Boolean existsById(long id) throws SQLException {
        return studentRepository.existsById(id);
    }

    public boolean exportToCSV(List<Long> studentIds, File csvFile) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile, false))) {
            writer.println("ID,Name,Score,Image,Address,Note");
            StudentQuery query = StudentQuery.builder()
                    .filter(StudentQuery.Filter.builder().inIds(studentIds).build())
                    .build();
            List<Student> students = studentRepository.findMany(query);

            for (Student student : students) {
                String name = student.getName() == null ? "" : student.getName();
                Double score = student.getScore() == null ? 0 : student.getScore();
                String image = student.getImage() == null ? "" : student.getImage();
                String address = student.getAddress() == null ? "" : student.getAddress();
                String note = student.getNote() == null ? "" : student.getNote();
                writer.printf("%s,%s,%.2f,%s,\"%s\",%s%n", student.getId(), name, score,
                        image, address  , note);
            }
             writer.flush();
            logger.info("Export successfully!");
            return true;
        } catch (IOException | SQLException e) {
            logger.info("Error exporting CSV: " + e.getMessage());
            return false;
        }
    }

    public boolean importFromCSV(File csvFile) throws SQLException {
        List<Student> students = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            List<String[]> records = reader.readAll();
            records.removeFirst(); // Skip header row

            for (String[] data : records) {
                if (data.length < 4) {
                    logger.warning("Skipping invalid row: " + String.join(",", data));
                    continue;
                }
                int index = 0;
//                long id = Long.parseLong(data[0]);
//                if (Boolean.TRUE.equals(studentRepository.existsById(id))) {
//                    logger.warning("Student with id: " + id + " already exists!");
//                    continue;
//                }
                Student student = Student.builder()
//                        .id(id)
                        .name(data[index++])
                        .score(Double.parseDouble(data[index++]))
//                        .image(data[index++])
                        .address(data[index++])
                        .note(data[index])
                        .build();
                students.add(student);
            }

            studentRepository.createMany(students);
            logger.info("Import successfully!");
            return true;
        } catch (IOException  | NumberFormatException | CsvException e) {
            logger.severe("Error importing CSV: " + e.getMessage());
            return false;
        }
    }
}
