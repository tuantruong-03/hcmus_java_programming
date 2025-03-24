package com.swing.context;

import com.mysql.cj.log.Log;
import com.swing.database.Database;
import com.swing.repository.student.StudentRepository;
import com.swing.repository.student.StudentRepositoryImpl;
import com.swing.services.student.StudentService;
import com.swing.services.student.StudentServiceImpl;
import lombok.Getter;

import java.sql.SQLException;

@Getter
public class ApplicationContext {
    private StudentRepository studentRepository;
    private StudentService studentService;


    private static ApplicationContext context;

    private ApplicationContext() {}

    public static ApplicationContext init(Database database) throws SQLException {
        if (context != null) {
            return context;
        }
        context = new ApplicationContext();
        context.studentRepository = new StudentRepositoryImpl(database);
        context.studentService = new StudentServiceImpl(context.studentRepository);
        return context;
    }
}
