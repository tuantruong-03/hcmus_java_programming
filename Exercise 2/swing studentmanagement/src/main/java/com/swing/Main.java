package com.swing;

import com.swing.database.Database;
import com.swing.models.Student;
import com.swing.repository.StudentRepository;
import com.swing.repository.query.Query;

import java.sql.SQLException;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException {
        Database.ConnectionOptions opts = new Database.ConnectionOptions("jdbc:mysql://localhost:3306", "student_management"
                ,"root"
                , "Tuantruong131203");
        Database db = new Database(opts);
        StudentRepository repo = new StudentRepository(db);
        test(repo);
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
        repository.add(student);
        List<Student> students = repository.findMany(Query.defaultInstance());

    }
}