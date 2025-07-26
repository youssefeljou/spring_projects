package com.example.ecommerce.dto;

import com.example.ecommerce.enums.Role;
import com.example.ecommerce.enums.Status;
import com.example.ecommerce.validation.annotation.ValidEmail;
import com.example.ecommerce.validation.annotation.ValidId;
import com.example.ecommerce.validation.annotation.ValidPassword;
import com.example.ecommerce.validation.annotation.ValidUsername;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @ValidId
    private Long id;
    @NotNull
    @NotBlank
    @ValidUsername
    private String username;

    @NotNull
    @NotBlank
    @ValidEmail
    private String email;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    @ValidPassword
    private String password;
    @NotNull
    //@NotBlank
    private List<String> phone;
    @NotNull
    //@NotBlank
    private Role role;

    public UserDto(String username,String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public UserDto(Long id,String username, String email, String name, String password, List<String> phone) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
    }
}

