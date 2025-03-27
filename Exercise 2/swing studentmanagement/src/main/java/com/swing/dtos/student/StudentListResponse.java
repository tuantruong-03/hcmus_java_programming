package com.swing.dtos.student;

import com.swing.repository.student.Student;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StudentListResponse {
    private long total;
    private List<StudentResponse> studentResponses;
}
