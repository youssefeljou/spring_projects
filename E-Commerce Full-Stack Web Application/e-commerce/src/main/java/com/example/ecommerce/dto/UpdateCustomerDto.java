package com.example.ecommerce.dto;

import com.example.ecommerce.validation.annotation.ValidEmail;
import com.example.ecommerce.validation.annotation.ValidId;
import com.example.ecommerce.validation.annotation.ValidUsername;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UpdateCustomerDto {

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
    //@NotBlank
    private List<String> phone;
}
