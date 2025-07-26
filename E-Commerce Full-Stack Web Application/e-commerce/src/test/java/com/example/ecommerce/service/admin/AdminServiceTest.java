package com.example.ecommerce.service.admin;

import com.example.ecommerce.config.TestSecurityConfig;
import com.example.ecommerce.dto.AdminDto;
import com.example.ecommerce.exception.InvalidInputException;
import com.example.ecommerce.mapper.AdminMapper;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.model.Admin;
import com.example.ecommerce.repository.AdminRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.product.ProductReviewService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AdminService.
 *    - createAdmin (business rules & encoding)
 *    - deleteAdmin (valid & invalid paths)
 *    - updateAdmin (happy‑path & edge cases)
 *    - getAdminById (happy‑path & failures)
 *    - method‑security using {@code @WithMockUser}
 */
@SpringJUnitConfig(classes = {
        AdminServiceTest.TestConfig.class,
        TestSecurityConfig.class
})
class AdminServiceTest {

    //Test‑configuration
    @TestConfiguration
    @ComponentScan(basePackageClasses = AdminService.class)
    static class TestConfig {

        @Bean
        public AdminRepository adminRepository() { return mock(AdminRepository.class); }

        @Bean
        public AdminMapper adminMapper() { return mock(AdminMapper.class); }

        @Bean
        public PasswordEncoder passwordEncoder() { return mock(PasswordEncoder.class); }

        @Bean public ProductRepository productRepository() { return mock(ProductRepository.class); }
        @Bean public ProductMapper productMapper()       { return mock(ProductMapper.class); }
        @Bean public ProductReviewService productReviewService() { return mock(ProductReviewService.class); }
    }

    //Autowired beans/mocks

    @Autowired private AdminService      adminService;
    @Autowired private AdminRepository   adminRepository;
    @Autowired private AdminMapper       adminMapper;
    @Autowired private PasswordEncoder   passwordEncoder;

    //----1)CREATE ADMIN----

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void createAdmin_success_encodesPassword_and_setsRole() {
        AdminDto dto = new AdminDto();
        dto.setUsername("root");
        dto.setEmail("root@shop.com");
        dto.setPassword("Plain#123");

        // Mock repository methods
        when(adminRepository.existsByEmail("root@shop.com")).thenReturn(false);
        when(adminRepository.existsByUsername("root")).thenReturn(false);

        // Mock password encoding
        when(passwordEncoder.encode("Plain#123")).thenReturn("bcrypt$hash");

        // Mock toEntity conversion
        when(adminMapper.toEntity(any(AdminDto.class))).thenAnswer(inv -> {
            AdminDto d = inv.getArgument(0);
            Admin a  = new Admin();
            a.setUsername(d.getUsername());
            a.setEmail(d.getEmail());
            a.setPassword(d.getPassword());
            return a;
        });

        // Mock toDto conversion
        when(adminMapper.toDto(any(Admin.class))).thenAnswer(inv -> {
            Admin a = inv.getArgument(0);
            AdminDto dto1 = new AdminDto();
            dto1.setUsername(a.getUsername());
            dto1.setEmail(a.getEmail());
            dto1.setPassword(a.getPassword()); // Ensure password is set correctly
            return dto1;
        });

        // Mock save method
        when(adminRepository.save(any(Admin.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Perform the actual test
        AdminDto result = adminService.addAdmin(dto);

        // Assertions
        assertThat(result.getPassword()).isEqualTo("bcrypt$hash");
        verify(passwordEncoder).encode("Plain#123");
    }





    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void createAdmin_fails_when_email_exists() {
        AdminDto dto = new AdminDto();
        dto.setUsername("root");
        dto.setEmail("dup@shop.com");
        dto.setPassword("pass");

        when(adminRepository.existsByEmail("dup@shop.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> adminService.addAdmin(dto),
                "Email is already in use");
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void createAdmin_fails_when_username_exists() {
        AdminDto dto = new AdminDto();
        dto.setUsername("dupUser");
        dto.setEmail("user@shop.com");
        dto.setPassword("pass");

        when(adminRepository.existsByEmail("user@shop.com")).thenReturn(false);
        when(adminRepository.existsByUsername("dupUser")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> adminService.addAdmin(dto),
                "Username is already in use");
    }

    //----2)DELETE ADMIN----

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteAdmin_success() {
        Long id = 5L;
        Admin stored = new Admin(); stored.setId(id);

        when(adminRepository.findById(id)).thenReturn(Optional.of(stored));

        adminService.deleteAdmin(id);

        verify(adminRepository).delete(stored);
    }


    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void deleteAdmin_fails_when_notFound() {
        when(adminRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException ex =
                assertThrows(EntityNotFoundException.class, () -> adminService.deleteAdmin(99L));

        assertEquals("Admin not found", ex.getMessage());
    }

    //3)----UPDATE ADMIN----

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void updateAdmin_ShouldUpdateSuccessfully_WhenAdminExists() {
        Long adminId = 1L;
        AdminDto inputDto = new AdminDto(adminId, "newUsername", "new@example.com", "New Name", "NewPass#789", List.of("0123456789"));

        Admin existingAdmin = new Admin();
        existingAdmin.setId(adminId);
        existingAdmin.setUsername("oldUsername");
        existingAdmin.setEmail("old@example.com");
        existingAdmin.setName("Old Name");
        existingAdmin.setPhone(List.of("0987654321"));

        Admin updatedAdmin = new Admin();
        updatedAdmin.setId(adminId);
        updatedAdmin.setUsername("newUsername");
        updatedAdmin.setEmail("new@example.com");
        updatedAdmin.setName("New Name");
        updatedAdmin.setPhone(List.of("0123456789"));
        updatedAdmin.setPassword("encodedPassword");

        when(adminRepository.findById(adminId)).thenReturn(Optional.of(existingAdmin));
        when(adminRepository.save(any(Admin.class))).thenReturn(updatedAdmin);
        when(adminMapper.toDto(updatedAdmin)).thenReturn(inputDto);

        AdminDto result = adminService.updateAdmin(adminId, inputDto);

        assertNotNull(result);
        assertEquals("newUsername", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void updateAdmin_ShouldThrow_WhenAdminNotFound() {
        Long adminId = 99L;
        AdminDto inputDto = new AdminDto(adminId, "noUser", "email", "no name", "pass", List.of("012"));

        when(adminRepository.findById(adminId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            adminService.updateAdmin(adminId, inputDto);
        });

        assertEquals("Admin not found", ex.getMessage());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void updateAdmin_ShouldFailAccess_WhenNotSuperAdmin() {
        Long adminId = 1L;
        AdminDto inputDto = new AdminDto(adminId, "user", "email@example.com", "Normal User", "123", List.of("012"));

        AuthorizationDeniedException ex = assertThrows(AuthorizationDeniedException.class, () -> {
            adminService.updateAdmin(adminId, inputDto);
        });

        assertEquals("Access Denied", ex.getMessage());
    }


    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void updateAdmin_ShouldUpdate_WhenOnlyNameAndPhoneAreChanged() {
        Long adminId = 1L;

        Admin existingAdmin = new Admin();
        existingAdmin.setId(adminId);
        existingAdmin.setName("Old Name");
        existingAdmin.setPhone(List.of("0123456789"));

        Admin savedAdmin = new Admin();
        savedAdmin.setId(adminId);
        savedAdmin.setName("New Name");
        savedAdmin.setPhone(List.of("0111111111"));

        AdminDto inputDto = new AdminDto(adminId, null, null, "New Name", "   ", List.of("0111111111"));
        AdminDto expectedDto = new AdminDto(adminId, null, null, "New Name", null, List.of("0111111111"));

        when(adminRepository.findById(adminId)).thenReturn(Optional.of(existingAdmin));
        when(adminRepository.save(existingAdmin)).thenReturn(savedAdmin);
        when(adminMapper.toDto(savedAdmin)).thenReturn(expectedDto);

        AdminDto result = adminService.updateAdmin(adminId, inputDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals(List.of("0111111111"), result.getPhone());
    }


    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void updateAdmin_ShouldThrow_WhenInputDtoIsNull() {
        assertThrows(NullPointerException.class, () -> adminService.updateAdmin(1L, null));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void updateAdmin_ShouldIgnoreBlankPassword() {
        Long adminId = 5L;
        Admin existingAdmin = new Admin();
        existingAdmin.setId(adminId);
        existingAdmin.setUsername("admin");
        existingAdmin.setEmail("admin@old.com");

        Admin savedAdmin = new Admin();
        savedAdmin.setId(adminId);
        savedAdmin.setUsername("adminNew");
        savedAdmin.setEmail("admin@new.com");

        AdminDto inputDto = new AdminDto(adminId, "adminNew", "admin@new.com", "name", "   ", List.of("012"));
        AdminDto expectedDto = inputDto;

        when(adminRepository.findById(adminId)).thenReturn(Optional.of(existingAdmin));
        when(adminRepository.save(existingAdmin)).thenReturn(savedAdmin);
        when(adminMapper.toDto(savedAdmin)).thenReturn(expectedDto);

        AdminDto result = adminService.updateAdmin(adminId, inputDto);

        assertNotNull(result);
        assertEquals("adminNew", result.getUsername());
        assertEquals("admin@new.com", result.getEmail());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void updateAdmin_ShouldValidateAndEncodePassword_WhenValidPasswordProvided() {
        Long adminId = 1L;

        Admin existingAdmin = new Admin();
        existingAdmin.setId(adminId);
        existingAdmin.setUsername("oldUser");
        existingAdmin.setEmail("old@example.com");

        AdminDto updatedDto = new AdminDto(
                adminId,
                "newUser",
                "new@example.com",
                "Admin Name",
                "Strong@Pass123",
                List.of("01012345678")
        );

        Admin savedAdmin = new Admin();
        savedAdmin.setId(adminId);
        savedAdmin.setUsername("newUser");
        savedAdmin.setEmail("new@example.com");
        savedAdmin.setPassword("encodedPassword");

        when(adminRepository.findById(adminId)).thenReturn(Optional.of(existingAdmin));
        when(passwordEncoder.encode("Strong@Pass123")).thenReturn("encodedPassword");
        when(adminRepository.save(any(Admin.class))).thenReturn(savedAdmin);
        when(adminMapper.toDto(savedAdmin)).thenReturn(updatedDto);

        AdminDto result = adminService.updateAdmin(adminId, updatedDto);

        assertNotNull(result);
        assertEquals("newUser", result.getUsername());
        assertEquals("new@example.com", result.getEmail());

        verify(passwordEncoder).encode("Strong@Pass123");
        verify(adminRepository).save(argThat(admin ->
                "encodedPassword".equals(admin.getPassword()) &&
                        "newUser".equals(admin.getUsername()) &&
                        "new@example.com".equals(admin.getEmail())
        ));
    }

    //----4)GET ADMIN BY ID----

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAdminById_ShouldReturnAdminDto_WhenAdminExists() {
        Long adminId = 1L;

        Admin admin = new Admin();
        admin.setId(adminId);
        admin.setUsername("adminUser");
        admin.setEmail("admin@example.com");
        admin.setName("Admin Name");
        admin.setPhone(List.of("01000000000"));

        AdminDto expectedDto = new AdminDto(adminId, "adminUser", "admin@example.com", "Admin Name", null, List.of("01000000000"));

        when(adminRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(adminMapper.toDto(admin)).thenReturn(expectedDto);

        AdminDto result = adminService.getAdminById(adminId);

        assertNotNull(result);
        assertEquals("adminUser", result.getUsername());
        assertEquals("admin@example.com", result.getEmail());
        assertEquals(List.of("01000000000"), result.getPhone());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void getAdminById_ShouldThrow_WhenAdminNotFound() {
        Long adminId = 99L;

        when(adminRepository.findById(adminId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            adminService.getAdminById(adminId);
        });

        assertEquals("Admin not found", ex.getMessage());
    }

}
