package com.example.education.service;

import com.example.education.dto.CourseDto;
import com.example.education.mapper.CourseMapper;
import com.example.education.model.Course;
import com.example.education.model.Master;
import com.example.education.model.Student;
import com.example.education.repository.CourseRepository;
import com.example.education.repository.MasterRepository;
import com.example.education.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseMapper courseMapper;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final MasterRepository masterRepository;

    public CourseDto findById(long id)
    {
        Course course=courseRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("course not found with id :"+ id));
        return courseMapper.mapToDto(course);
    }

    public CourseDto addCourse(CourseDto courseDto)
    {
        Course course=courseMapper.mapToEntity(courseDto);
        courseRepository.save(course);
        return courseMapper.mapToDto(course);
    }

    public Course findByIdWithAllInfo(long id)
    {
        Course course=courseRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("course not found with id :"+ id));
        return course;
    }

    public List<CourseDto> findAll()
    {
        List<Course> courses= courseRepository.findAll();
        return courseMapper.mapToDto(courses);
    }

    public List<Course> findAllWithAllInfo()
    {
        List<Course> courses= courseRepository.findAll();
        return courses;
    }

    public void enrollStudent(long studentId,long courseId)
    {
        Student student=studentRepository.findById(studentId).orElseThrow(
                () -> new IllegalArgumentException("student not found with id "+studentId));

        Course course=courseRepository.findById(courseId).orElseThrow(
                () -> new IllegalArgumentException("course not found with id "+courseId));

        course.getStudents().add(student);
        courseRepository.save(course);

    }

    public void removeStudent(long studentId,long courseId)
    {
        Student student=studentRepository.findById(studentId).orElseThrow(
                () -> new IllegalArgumentException("student not found with id "+studentId));

        Course course=courseRepository.findById(courseId).orElseThrow(
                () -> new IllegalArgumentException("course not found with id "+courseId));

        course.getStudents().remove(student);
        courseRepository.save(course);
    }

    public void enrollMaster(long masterId,long courseId)
    {
        Master master=masterRepository.findById(masterId).orElseThrow(
                () -> new IllegalArgumentException("master not found with id "+masterId));

        Course course=courseRepository.findById(courseId).orElseThrow(
                () -> new IllegalArgumentException("course not found with id "+courseId));

        course.getMasters().add(master);
        courseRepository.save(course);

    }

    public void removeMaster(long masterId,long courseId)
    {
        Master master=masterRepository.findById(masterId).orElseThrow(
                () -> new IllegalArgumentException("master not found with id "+masterId));

        Course course=courseRepository.findById(courseId).orElseThrow(
                () -> new IllegalArgumentException("course not found with id "+courseId));

        course.getMasters().remove(master);
        courseRepository.save(course);
    }

}
