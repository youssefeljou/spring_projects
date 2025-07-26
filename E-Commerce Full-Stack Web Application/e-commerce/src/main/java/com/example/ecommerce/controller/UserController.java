package com.example.ecommerce.controller;

import com.example.ecommerce.dto.LoginDto;
import com.example.ecommerce.dto.PasswordResetDto;
import com.example.ecommerce.service.customer.CustomerLoginService;
import com.example.ecommerce.service.user.CustomerAdminUserDetailsService;
import com.example.ecommerce.service.user.UserAuthService;
import com.example.ecommerce.service.user.UserLoginAttemptService;
import com.example.ecommerce.service.user.UserProfileService;
import com.example.ecommerce.session.AuthenticatedUserInfo;
import com.example.ecommerce.validation.InputValidator;
import com.example.ecommerce.validation.LoginValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Slf4j
@Validated
@Tag(name = "User Authentication & Profile", description = "Endpoints for user login, password reset, and session handling")
public class UserController {

    private final UserAuthService userAuthService;
    private final UserLoginAttemptService userLoginAttemptService;
    private final CustomerLoginService customerLoginService;
    private final LoginValidator loginValidator;
    private final AuthenticatedUserInfo authenticatedUserInfo;
    private final CustomerAdminUserDetailsService customerAdminUserDetailsService;
    private final UserProfileService userProfileService;

    public UserController(UserAuthService userAuthService,
                          UserLoginAttemptService userLoginAttemptService,
                          CustomerLoginService customerLoginService,
                          LoginValidator loginValidator,
                          AuthenticatedUserInfo authenticatedUserInfo,
                          CustomerAdminUserDetailsService customerAdminUserDetailsService,
                          UserProfileService userProfileService) {
        this.userAuthService = userAuthService;
        this.userLoginAttemptService = userLoginAttemptService;
        this.customerLoginService = customerLoginService;
        this.loginValidator = loginValidator;
        this.authenticatedUserInfo = authenticatedUserInfo;
        this.customerAdminUserDetailsService = customerAdminUserDetailsService;
        this.userProfileService = userProfileService;
    }

    @Operation(
            summary = "Login user (username or email)",
            requestBody = @RequestBody(
                    required = true,
                    description = "Username/email and password",
                    content = @Content(schema = @Schema(implementation = LoginDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful"),
                    @ApiResponse(responseCode = "400", description = "Validation error"),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials, blocked user, or deactivated account"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @org.springframework.web.bind.annotation.RequestBody @Valid LoginDto loginDto,
            @Parameter(hidden = true) HttpSession session) {

        String key = loginDto.getKey();
        String password = loginDto.getPassword();

        String username = null;
        String email = null;

        if (InputValidator.isValidEmail(key)) {
            email = key;
        } else {
            username = key;
        }

        try {
            String validatedKey = loginValidator.validate(username, email);
            loginValidator.checkIfBlocked(validatedKey);
            userAuthService.checkCustomerStatus(username, email);

            UserDetails userDetails = userAuthService.authenticateAndGetUserDetails(username, email, password);
            loginValidator.handleLoginAttempt(validatedKey, true);

            AuthenticatedUserInfo info = customerAdminUserDetailsService.getAuthenticatedUserInfo();

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authToken);
            SecurityContextHolder.setContext(securityContext);

            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
            session.setAttribute("userId", info.getId());
            session.setAttribute("username", info.getUsername());

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "id", info.getId(),
                    "username", info.getUsername()
            ));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            loginValidator.handleLoginAttempt(key, false);

            String safeMessage = switch (ex.getMessage()) {
                case "Customer account is deactivated." -> "Your account is deactivated. Please verify it and try again.";
                case "Too many failed login attempts. Please try again later." -> "Too many login attempts. Please try again later.";
                case "Incorrect credentials." -> "Invalid username or password.";
                default -> "Login failed. Please try again.";
            };
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(safeMessage);
        }
    }

    @Operation(
            summary = "Request a password reset email",
            parameters = @Parameter(name = "username", description = "The username to request password reset for"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reset link sent"),
                    @ApiResponse(responseCode = "404", description = "User not found with this username"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/request-reset")
    public ResponseEntity<String> requestPasswordReset(
            @RequestParam @NotBlank String username) {
        log.info("Password reset requested for username: {}", username);
        userProfileService.requestPasswordReset(username);
        return ResponseEntity.ok("Password reset link sent to your email.");
    }

    @Operation(
            summary = "Reset password using token",
            requestBody = @RequestBody(
                    required = true,
                    description = "Token and new password",
                    content = @Content(schema = @Schema(implementation = PasswordResetDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password reset successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid or expired token, or weak password"),
                    @ApiResponse(responseCode = "404", description = "User associated with token not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PatchMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @org.springframework.web.bind.annotation.RequestBody @Valid PasswordResetDto request) {
        log.info("Attempting password reset using token: {}", request.getToken());
        userProfileService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password has been reset successfully.");
    }

    @Operation(
            summary = "Logout user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Logout successful")
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Parameter(hidden = true) HttpSession session) {
        log.info("Session Id to invalidate : {}", session.getId());
        session.invalidate();
        return ResponseEntity.ok("Logout successful.");
    }

    @Operation(
            summary = "Change password (Not Implemented)",
            responses = {
                    @ApiResponse(responseCode = "501", description = "Not implemented yet")
            }
    )
    @PostMapping("/change-password")
    public void changePassword(@RequestParam String newPassword) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Operation(
            summary = "Verify user using OTP (Not Implemented)",
            responses = {
                    @ApiResponse(responseCode = "501", description = "Not implemented yet")
            }
    )
    @GetMapping("/verify")
    public boolean verify(@RequestParam String otp) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
