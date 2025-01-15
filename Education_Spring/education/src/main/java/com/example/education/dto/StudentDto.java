package com.example.education.dto;

import com.example.education.model.Course;

import java.util.List;

public record StudentDto(String firstName,
                         String lastName,
                         Long userId,
                         List<Course> courses) {
}
