package com.example.education.mapper;

import com.example.education.dto.UserDto;
import com.example.education.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto mapToDto(User user);
    List<UserDto> mapToDto(List<User> users);

    User mapToEntity(UserDto userDto);
}
