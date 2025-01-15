package com.example.education.controller;

import com.example.education.dto.RoleDto;
import com.example.education.dto.UserDto;
import com.example.education.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleDto> createRole(@RequestParam String roleName) {
        RoleDto roleDto = roleService.createRole(roleName);
        return new ResponseEntity<>(roleDto, HttpStatus.CREATED);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable long id) {
        RoleDto roleDto = roleService.findRole(id);
        return ResponseEntity.ok(roleDto);
    }

    @GetMapping("/id/{id}/info")
    public ResponseEntity<?> getRoleWithAllInfo(@PathVariable long id) {
        return ResponseEntity.ok(roleService.findRoleWithAllInfo(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        List<RoleDto> roles = roleService.findAll();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/all/info")
    public ResponseEntity<List<?>> getAllRolesWithInfo() {
        return ResponseEntity.ok(roleService.findAllWithAllInfo());
    }

    @PostMapping("/{roleId}/user/{userId}")
    public ResponseEntity<UserDto> assignRoleToUser(@PathVariable long roleId, @PathVariable long userId) {
        UserDto userDto = roleService.assignRoleToUser(roleId, userId);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/{roleId}/user/{userId}")
    public ResponseEntity<UserDto> removeRoleFromUser(@PathVariable long roleId, @PathVariable long userId) {
        UserDto userDto = roleService.removeRoleFromUser(roleId, userId);
        return ResponseEntity.ok(userDto);
    }
}