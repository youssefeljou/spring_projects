package com.example.education.mapper;

import com.example.education.dto.AdminDto;
import com.example.education.model.Admin;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    AdminDto mapToDto(Admin admin);
    List<AdminDto> mapToDto(List<Admin> admins);

    Admin mapToEntity(AdminDto adminDto);

}
