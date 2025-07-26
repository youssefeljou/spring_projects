package com.example.ecommerce.service.admin;

import com.example.ecommerce.dto.AdminDto;
import com.example.ecommerce.enums.Role;
import com.example.ecommerce.exception.InvalidInputException;
import com.example.ecommerce.mapper.AdminMapper;
import com.example.ecommerce.model.Admin;
import com.example.ecommerce.repository.AdminRepository;
import com.example.ecommerce.validation.InputValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminDto addAdmin(AdminDto adminDto) {
        // Validate input
        if (adminDto == null) {
            throw new InvalidInputException("AdminDto cannot be null");
        }

        // Check if the email already exists
        if (adminRepository.existsByEmail(adminDto.getEmail())) {
            throw new InvalidInputException("Email already exists");
        }

        // Check if the username already exists
        if (adminRepository.existsByUsername(adminDto.getUsername())) {
            throw new InvalidInputException("Username already exists");
        }

        // Encode the password
        adminDto.setPassword(passwordEncoder.encode(adminDto.getPassword()));
        log.info("Admin with password: {}", adminDto.getPassword());

        // Map the DTO to the entity
        Admin admin = adminMapper.toEntity(adminDto);

        // Ensure the entity was mapped properly
        if (admin == null) {
            throw new RuntimeException("Error mapping admin entity");
        }

        // Save the admin entity
        Admin savedAdmin = adminRepository.save(admin); // Save the entity, not the DTO

        // Return the saved admin as a DTO
        return adminMapper.toDto(savedAdmin);
    }

    public String deleteAdmin(Long adminId) {

        // Check if the admin exists
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));

        // Delete the admin
        adminRepository.delete(admin);

        log.info("Admin with ID {} successfully deleted", adminId);
        return "Admin successfully deleted"; // 200 OK
    }

    // Only super admin can update admin details
    @PreAuthorize("hasRole('SUPERADMIN')")
    @Transactional
    public AdminDto updateAdmin(Long adminId, AdminDto updatedAdminDto) {

        if (updatedAdminDto == null) {
            throw new NullPointerException("Updated AdminDto cannot be null");
        }

        // checking if the admin exists
        Admin existingAdmin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));


        boolean updated = false;

        // Check if the email is being updated and already exists
        if (updatedAdminDto.getEmail() != null) {
            Optional<Admin> existingAdminWithEmail = adminRepository.findByEmail(updatedAdminDto.getEmail());
            if (existingAdminWithEmail.isPresent() && !existingAdminWithEmail.get().getId().equals(adminId)) {
                throw new InvalidInputException("Email already exists");
            }
        }

        // Check if the username is being updated and already exists
        if (updatedAdminDto.getUsername() != null) {
            Optional<Admin> existingAdminWithUsername = adminRepository.findByUsername(updatedAdminDto.getUsername());
            if (existingAdminWithUsername.isPresent() && !existingAdminWithUsername.get().getId().equals(adminId)) {
                throw new InvalidInputException("Username already exists");
            }
        }

        // Update username only
        if (isDifferent(updatedAdminDto.getUsername(), existingAdmin.getUsername())) {
            existingAdmin.setUsername(updatedAdminDto.getUsername());
            updated = true;
        }

        // Update email only
        if (isDifferent(updatedAdminDto.getEmail(), existingAdmin.getEmail())) {
            existingAdmin.setEmail(updatedAdminDto.getEmail());
            updated = true;
        }

        // Update password
        if (updatedAdminDto.getPassword() != null && !updatedAdminDto.getPassword().isBlank()) {
            if (!InputValidator.isValidPassword(updatedAdminDto.getPassword())) {
                throw new IllegalArgumentException("Invalid password format");
            }
            existingAdmin.setPassword(passwordEncoder.encode(updatedAdminDto.getPassword()));
            updated = true;
        }

        // Name and phone
        if (updatedAdminDto.getName() != null &&
                !updatedAdminDto.getName().equals(existingAdmin.getName())) {
            existingAdmin.setName(updatedAdminDto.getName());
            updated = true;
        }

        if (updatedAdminDto.getPhone() != null &&
                !updatedAdminDto.getPhone().equals(existingAdmin.getPhone())) {
            existingAdmin.setPhone(updatedAdminDto.getPhone());
            updated = true;
        }

        Admin savedAdmin = adminRepository.save(existingAdmin);
        return adminMapper.toDto(savedAdmin);
    }

    private boolean isDifferent(String newValue, String oldValue) {
        return newValue != null && !newValue.equals(oldValue);
    }

    public AdminDto getAdminById(Long adminId) {

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));

        return adminMapper.toDto(admin);
    }
}
