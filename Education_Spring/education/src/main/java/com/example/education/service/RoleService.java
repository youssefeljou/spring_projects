package com.example.education.service;

import com.example.education.dto.RoleDto;
import com.example.education.dto.UserDto;
import com.example.education.mapper.RoleMapper;
import com.example.education.mapper.UserMapper;
import com.example.education.model.Role;
import com.example.education.model.User;
import com.example.education.repository.RoleRepository;
import com.example.education.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public RoleDto createRole(String roleName)
    {
        Role role=new Role();
        role.setRoleName(roleName);
        roleRepository.save(role);
        return roleMapper.mapToDto(role);
    }

    public RoleDto findRole(long id)
    {
        Role role=roleRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("role not found with this id" +id));
        return roleMapper.mapToDto(role);
    }

    public Role findRoleWithAllInfo(long id)
    {
        Role role=roleRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("role not found with this id" +id));
        return role;
    }

    public List<RoleDto> findAll()
    {
        List<Role> roles=roleRepository.findAll();
        return roleMapper.mapToDto(roles);
    }

    public List<Role> findAllWithAllInfo()
    {
        List<Role> roles=roleRepository.findAll();
        return roles;
    }

    public UserDto assignRoleToUser(long roleId,long userId)
    {
        User user=userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("user not found with this id:"+userId));
        Role role=roleRepository.findById(roleId).orElseThrow(
                ()->new IllegalArgumentException("role not found with this id"+roleId));
        user.getRoles().add(role);
        userRepository.save(user);
        return userMapper.mapToDto(user);
    }

    public UserDto removeRoleFromUser(long roleId,long userId)
    {
        User user=userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("user not found with this id:"+userId));
        Role role=roleRepository.findById(roleId).orElseThrow(
                ()->new IllegalArgumentException("role not found with this id"+roleId));
        user.getRoles().remove(role);
        userRepository.save(user);
        return userMapper.mapToDto(user);
    }

}
