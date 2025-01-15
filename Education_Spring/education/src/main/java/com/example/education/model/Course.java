package com.example.education.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Course extends BaseEntity{

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "course_price")
    private String coursePrice;

    @ManyToMany(mappedBy = "courses")
    private List<Student> students;



    @OneToMany(fetch = FetchType.EAGER,mappedBy = "course")
    private List<Master> masters;
}
