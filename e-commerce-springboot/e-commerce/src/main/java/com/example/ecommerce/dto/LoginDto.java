package com.example.ecommerce.dto;

import com.example.ecommerce.validation.annotation.ValidEmail;
import com.example.ecommerce.validation.annotation.ValidPassword;
import com.example.ecommerce.validation.annotation.ValidUsername;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    @NotBlank
    @NotNull
    private String key;   // this is the single input: username or email

    @NotNull
    @NotBlank
    private String password;

    // getters and setters
}