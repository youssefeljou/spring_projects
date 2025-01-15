package com.example.education.service;

import com.example.education.dto.UserDto;
import com.example.education.mapper.UserMapper;
import com.example.education.model.User;
import com.example.education.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AdminRepository adminRepository;
    private final MasterRepository masterRepository;
    private final StudentRepository studentRepository;
    private final UserMapper userMapper;

    public UserDto createUser(UserDto userDto)
    {
        User user=userMapper.mapToEntity(userDto);
        userRepository.save(user);
        return userMapper.mapToDto(user);
    }

    public UserDto findById(long id)
    {
        User user=userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("user not found with this user id:"+id));
        return userMapper.mapToDto(user);
    }
    public User findByIdWithAllInfo(long id)
    {
        User user=userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("user not found with this user id:"+id));
        return user;
    }
    public List<UserDto> findAll()
    {
        List<User> users=userRepository.findAll();
        return userMapper.mapToDto(users);
    }
    public List<User> findAllWithAllInfo()
    {
        List<User> users=userRepository.findAll();
        return users;
    }

}
