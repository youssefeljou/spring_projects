package com.example.education.mapper;

import com.example.education.dto.AdminDto;
import com.example.education.dto.CourseDto;
import com.example.education.model.Admin;
import com.example.education.model.Course;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseDto mapToDto(Course course);
    List<CourseDto> mapToDto(List<Course> courses);

    Course mapToEntity(CourseDto courseDto);
}
