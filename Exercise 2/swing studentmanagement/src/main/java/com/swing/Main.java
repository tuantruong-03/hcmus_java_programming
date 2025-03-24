package com.swing;

import com.swing.context.ApplicationContext;
import com.swing.context.DatabaseConnectionForm;
import com.swing.database.Database;
import com.swing.models.Student;
import com.swing.repository.student.StudentQuery;
import com.swing.repository.student.StudentRepository;
import com.swing.repository.student.StudentRepositoryImpl;

import java.sql.SQLException;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException {
        DatabaseConnectionForm form = new DatabaseConnectionForm();
    }

    private static void test (StudentRepository repository) throws SQLException {
        Student student = Student.builder()
                .name("John Doe")
                .score(85.5)
                .image("john_doe.jpg")
                .address("123 Main St, City")
                .note("Top performer")
                .build();

        // Test adding a student
        repository.create(student);
        List<Student> students = repository.findMany(StudentQuery.defaultInstance());
        repository.existsById(1);

    }
}