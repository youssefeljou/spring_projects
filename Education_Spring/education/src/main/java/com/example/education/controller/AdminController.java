package com.example.education.controller;

import com.example.education.dto.AdminDto;
import com.example.education.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/id/{id}")
    public ResponseEntity<AdminDto> getAdminById(@PathVariable long id) {
        AdminDto adminDto = adminService.findAdminById(id);
        return ResponseEntity.ok(adminDto);
    }

    @GetMapping("/id/{id}/info")
    public ResponseEntity<?> getAdminWithUserInfo(@PathVariable long id) {
        return ResponseEntity.ok(adminService.findAdminByIdWithUserInfo(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<AdminDto>> getAllAdmins() {
        List<AdminDto> adminDtos = adminService.findAllAdmins();
        return ResponseEntity.ok(adminDtos);
    }

    @GetMapping("/all/info")
    public ResponseEntity<List<?>> getAllAdminsWithUserInfo() {
        return ResponseEntity.ok(adminService.findAllAdminsWithUserInfo());
    }

    @PostMapping
    public ResponseEntity<AdminDto> createAdmin(@RequestBody AdminDto adminDto) {
        AdminDto createdAdmin = adminService.createAdmin(adminDto);
        return new ResponseEntity<>(createdAdmin, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAdmin(@PathVariable long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.ok("Admin deleted successfully.");
    }
}

