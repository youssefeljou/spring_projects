package com.example.education.mapper;

import com.example.education.dto.RoleDto;
import com.example.education.model.Role;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDto mapToDto(Role role);
    List<RoleDto> mapToDto(List<Role> roles);

    Role mapToEntity(RoleDto roleDto);
}
