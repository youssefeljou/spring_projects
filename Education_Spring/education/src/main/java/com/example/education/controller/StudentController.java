package com.example.education.controller;

import com.example.education.dto.StudentDto;
import com.example.education.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentDto> createStudent(@RequestBody StudentDto studentDto) {
        StudentDto createdStudent = studentService.createStudent(studentDto);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable long id) {
        StudentDto studentDto = studentService.findById(id);
        return ResponseEntity.ok(studentDto);
    }

    @GetMapping("/id/{id}/info")
    public ResponseEntity<?> getStudentWithAllInfo(@PathVariable long id) {
        return ResponseEntity.ok(studentService.findByIdWithAllInfo(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        List<StudentDto> students = studentService.findAll();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/all/info")
    public ResponseEntity<List<?>> getAllStudentsWithInfo() {
        return ResponseEntity.ok(studentService.findAllWithAllInfo());
    }

    @PostMapping("/{studentId}/course/{courseId}")
    public ResponseEntity<StudentDto> assignCourseToStudent(@PathVariable long courseId, @PathVariable long studentId) {
        StudentDto updatedStudent = studentService.assignCourse(courseId, studentId);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{studentId}/course/{courseId}")
    public ResponseEntity<StudentDto> removeCourseFromStudent(@PathVariable long courseId, @PathVariable long studentId) {
        StudentDto updatedStudent = studentService.removeCourse(courseId, studentId);
        return ResponseEntity.ok(updatedStudent);
    }
}