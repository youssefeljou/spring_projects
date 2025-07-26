package com.example.ecommerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class LoginAttempt {

    @Id
    private Long id;

    private int attempts;

    private LocalDateTime lastAttempt;


}
