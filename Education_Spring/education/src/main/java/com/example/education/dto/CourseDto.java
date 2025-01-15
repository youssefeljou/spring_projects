package com.example.education.dto;

import java.util.List;

public record CourseDto(String courseName,
                        String coursePrice,
                        List<Long> studentIds,
                        List<Long> masterIds) {
}
