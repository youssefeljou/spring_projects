package com.example.education.service;

import com.example.education.dto.MasterDto;
import com.example.education.mapper.MasterMapper;
import com.example.education.model.Course;
import com.example.education.model.Master;
import com.example.education.model.User;
import com.example.education.repository.CourseRepository;
import com.example.education.repository.MasterRepository;
import com.example.education.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterService {

    private final MasterMapper masterMapper;
    private final UserRepository userRepository;
    private final MasterRepository masterRepository;
    private final CourseRepository courseRepository;

    public MasterDto createMaster(MasterDto masterDto)
    {
        User user=userRepository.findById(masterDto.userId()).orElseThrow(
                () -> new IllegalArgumentException("this user not found with id:"+masterDto.userId()));

        Course course=courseRepository.findById(masterDto.courseId()).orElseThrow(
                () -> new IllegalArgumentException("this course not found with id:"+masterDto.courseId()));

        Master master=masterMapper.mapToEntity(masterDto);
        master.setUser(user);
        master.setCourse(course);
        masterRepository.save(master);
        return masterMapper.mapToDto(master);
    }

    public Master findByIdWithAllInfo(long id)
    {
        Master master=masterRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("this master not found with id:"+id));
        return master;
    }

    public MasterDto findById(long id)
    {
        Master master=masterRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("this master not found with id:"+id));
        return masterMapper.mapToDto(master);
    }

    public List<MasterDto> findAll()
    {
        List<Master> masters=masterRepository.findAll();
        return masterMapper.mapToDto(masters);
    }

    public List<Master> findAllWithAllInfo()
    {
        List<Master> masters=masterRepository.findAll();
        return masters;
    }

    public MasterDto assignCourse(long courseId,long masterId)
    {
        Master master=masterRepository.findById(masterId).orElseThrow(
                () -> new IllegalArgumentException("this master not found with id:"+masterId));
        Course course=courseRepository.findById(courseId).orElseThrow(
                () -> new IllegalArgumentException("this course not found with id:"+courseId));
        master.setCourse(course);
        masterRepository.save(master);
        return masterMapper.mapToDto(master);
    }

    public MasterDto removeCourse(long masterId)
    {
        Master master=masterRepository.findById(masterId).orElseThrow(
                () -> new IllegalArgumentException("this master not found with id:"+masterId));
        master.setCourse(null);
        masterRepository.save(master);
        return masterMapper.mapToDto(master);
    }

}
