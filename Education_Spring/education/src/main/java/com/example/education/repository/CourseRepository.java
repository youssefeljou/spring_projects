package com.example.education.repository;

import com.example.education.model.Admin;
import com.example.education.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course,Long>
{

}
