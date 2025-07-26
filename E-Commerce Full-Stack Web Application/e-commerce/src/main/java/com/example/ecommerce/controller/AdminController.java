package com.example.ecommerce.controller;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.exception.InvalidInputException;
import com.example.ecommerce.service.admin.AdminProductManagementService;
import com.example.ecommerce.service.admin.AdminService;
import com.example.ecommerce.service.admin.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "APIs for admin and product management")
public class AdminController {

    private final AdminUserService adminUserService;
    private final AdminProductManagementService adminProductManagementService;
    private final AdminService adminService;


    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Add admin", description = "Add a new admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Admin created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/add")
    public ResponseEntity<AdminDto> addAdmin(@RequestBody @Valid AdminDto adminDto) {
        try {
            AdminDto createdAdmin = adminService.addAdmin(adminDto);
            return ResponseEntity.status(201).body(createdAdmin); // 201 Created
        } catch (InvalidInputException e) {
            return ResponseEntity.badRequest().body(null); // 400 Bad Request
        }
    }

    @Operation(summary = "Update admin", description = "Update admin by ID (Super Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or no changes detected"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Admin not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<String> updateAdmin(@PathVariable Long id,
                                              @RequestBody AdminDto adminDto) {
        try {
            adminService.updateAdmin(id, adminDto);
            return ResponseEntity.ok("Status: succeeded"); // 200 OK
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found"); // 404 Not Found
        } catch (IllegalArgumentException | InvalidInputException e) {
            return ResponseEntity.badRequest().body("Invalid input or no changes detected"); // 400 Bad Request
        }
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @Operation(summary = "Delete admin", description = "Delete admin by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Admin deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Admin not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAdmin(@PathVariable Long id) {
        try {
            adminService.deleteAdmin(id);
            return ResponseEntity.ok("Admin successfully deleted"); // 200 OK
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found"); // 404 Not Found
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid admin ID"); // 400 Bad Request
        }
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    @Operation(summary = "Add product", description = "Add a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/items/add")
    public ResponseEntity<ProductPublicDTO> addItem(@Valid @RequestBody ProductCreateDTO dto,
                                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<>();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
            }
            throw new InvalidInputException(String.join("; ", errors));
        }

        ProductPublicDTO created = adminProductManagementService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    @Operation(summary = "Update product", description = "Update existing product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/items/update/{id}")
    public ResponseEntity<ProductPublicDTO> updateItem(@PathVariable @Positive Long id,
                                                       @Valid @RequestBody ProductAdminDTO dto,
                                                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<String>();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
            }
            throw new InvalidInputException(String.join("; ", errors));
        }

        ProductPublicDTO updated = adminProductManagementService.updateProduct(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    @Operation(summary = "Patch product status", description = "Update status of a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status input"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/items/status/{id}")
    public ResponseEntity<Void> patchStatus(@PathVariable @Positive Long id,
                                            @Valid @RequestBody PatchProductStatusDTO dto,
                                            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<String> errors = new ArrayList<String>();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
            }
            throw new InvalidInputException(String.join("; ", errors));
        }

        adminProductManagementService.patchStatus(id, dto.getStatus());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','ADMIN')")
    @Operation(summary = "Delete product", description = "Delete a product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/items/delete/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable @Positive Long id) {
        adminProductManagementService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}

