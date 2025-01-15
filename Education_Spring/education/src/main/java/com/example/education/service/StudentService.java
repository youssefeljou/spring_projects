package com.example.education.service;

import com.example.education.dto.StudentDto;
import com.example.education.dto.StudentDto;
import com.example.education.mapper.StudentMapper;
import com.example.education.model.Course;
import com.example.education.model.Master;
import com.example.education.model.Student;
import com.example.education.model.User;
import com.example.education.repository.CourseRepository;
import com.example.education.repository.StudentRepository;
import com.example.education.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentMapper studentMapper;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public StudentDto createStudent(StudentDto studentDto)
    {

        User user = userRepository.findById(studentDto.userId()).orElseThrow(
                () -> new IllegalArgumentException("This user not found with id: " + studentDto.userId()));

        // Get the list of courses directly from the studentDto (no need to fetch by IDs)
        List<Course> courses = studentDto.courses();

        // Ensure courses are not empty
        if (courses == null || courses.isEmpty()) {
            throw new IllegalArgumentException("No courses provided for the student.");
        }

        Student student = studentMapper.mapToEntity(studentDto);
        student.setUser(user);
        student.setCourses(courses);

        studentRepository.save(student);

        return studentMapper.mapToDto(student);
    }

    public Student findByIdWithAllInfo(long id)
    {
        Student student=studentRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("this user not found with id:"+id));
        return student;
    }

    public StudentDto findById(long id)
    {
        Student student=studentRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("this student not found with id:"+id));
        return studentMapper.mapToDto(student);
    }

    public List<StudentDto> findAll()
    {
        List<Student> students=studentRepository.findAll();
        return studentMapper.mapToDto(students);
    }

    public List<Student> findAllWithAllInfo()
    {
        List<Student> students=studentRepository.findAll();
        return students;
    }

    public StudentDto assignCourse(long courseId,long studentId)
    {
        Student student=studentRepository.findById(studentId).orElseThrow(
                () -> new IllegalArgumentException("this student not found with id:"+studentId));
        Course course=courseRepository.findById(courseId).orElseThrow(
                () -> new IllegalArgumentException("this course not found with id:"+courseId));
        student.getCourses().add(course);
        studentRepository.save(student);
        return studentMapper.mapToDto(student);
    }

    public StudentDto removeCourse(long courseId,long studentId)
    {
        Student student=studentRepository.findById(studentId).orElseThrow(
                () -> new IllegalArgumentException("this student not found with id:"+studentId));
        Course course=courseRepository.findById(courseId).orElseThrow(
                () -> new IllegalArgumentException("this course not found with id:"+courseId));
        student.getCourses().remove(course);
        studentRepository.save(student);
        return studentMapper.mapToDto(student);
    }

}
