package com.example.education.controller;

import com.example.education.dto.UserDto;
import com.example.education.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        UserDto createdUser = userService.createUser(userDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable long id) {
        UserDto userDto = userService.findById(id);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/id/{id}/info")
    public ResponseEntity<?> getUserWithAllInfo(@PathVariable long id) {
        return ResponseEntity.ok(userService.findByIdWithAllInfo(id));
    }

    @GetMapping("all")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/all/info")
    public ResponseEntity<List<?>> getAllUsersWithInfo() {
        return ResponseEntity.ok(userService.findAllWithAllInfo());
    }
}