package com.example.education.mapper;

import com.example.education.dto.StudentDto;
import com.example.education.model.Student;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    StudentDto mapToDto(Student student);
    List<StudentDto> mapToDto(List<Student> students);

    Student mapToEntity(StudentDto studentDto);
}
