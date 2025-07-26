package com.example.ecommerce.mapper;


import com.example.ecommerce.dto.UserDto;
import com.example.ecommerce.dto.AdminDto;

import com.example.ecommerce.dto.AdminDto;
import com.example.ecommerce.model.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface AdminMapper {

    AdminDto toDto(Admin admin);   // ✅ distinct name
    @Mapping(target = "id", ignore = true)

    Admin toEntity(AdminDto dto);     // ✅ distinct name

    /***
     * I added an admin mapper that maps from entity to dto and from dto to entity
     */
}
