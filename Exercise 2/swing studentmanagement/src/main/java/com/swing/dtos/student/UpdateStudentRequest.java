package com.swing.dtos.student;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class UpdateStudentRequest {
    private String name;
    private Double score;
    private String image;
    private String address;
    private String note;
}
