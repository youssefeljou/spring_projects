package com.example.education.controller;

import com.example.education.dto.CourseDto;
import com.example.education.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/id/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable long id) {
        CourseDto courseDto = courseService.findById(id);
        return ResponseEntity.ok(courseDto);
    }

    @GetMapping("/id/{id}/info")
    public ResponseEntity<?> getCourseWithAllInfo(@PathVariable long id) {
        return ResponseEntity.ok(courseService.findByIdWithAllInfo(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        List<CourseDto> courses = courseService.findAll();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/all/info")
    public ResponseEntity<List<?>> getAllCoursesWithInfo() {
        return ResponseEntity.ok(courseService.findAllWithAllInfo());
    }

    @PostMapping
    public ResponseEntity<CourseDto> addCourse(@RequestBody CourseDto courseDto) {
        CourseDto createdCourse = courseService.addCourse(courseDto);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    @PostMapping("/{courseId}/student/{studentId}")
    public ResponseEntity<String> enrollStudent(@PathVariable long studentId, @PathVariable long courseId) {
        courseService.enrollStudent(studentId, courseId);
        return ResponseEntity.ok("Student enrolled successfully.");
    }

    @DeleteMapping("/{courseId}/student/{studentId}")
    public ResponseEntity<String> removeStudent(@PathVariable long studentId, @PathVariable long courseId) {
        courseService.removeStudent(studentId, courseId);
        return ResponseEntity.ok("Student removed successfully.");
    }

    @PostMapping("/{courseId}/master/{masterId}")
    public ResponseEntity<String> enrollMaster(@PathVariable long masterId, @PathVariable long courseId) {
        courseService.enrollMaster(masterId, courseId);
        return ResponseEntity.ok("Master enrolled successfully.");
    }

    @DeleteMapping("/{courseId}/master/{masterId}")
    public ResponseEntity<String> removeMaster(@PathVariable long masterId, @PathVariable long courseId) {
        courseService.removeMaster(masterId, courseId);
        return ResponseEntity.ok("Master removed successfully.");
    }
}