package com.example.education.dto;

import com.example.education.model.Role;

import java.util.List;

public record UserDto(String userName,
                      String password, // Depending on security, consider omitting or masking this field.
                      List<Role> roles,
                      Long studentId,
                      Long masterId,
                      Long adminId) {
}
