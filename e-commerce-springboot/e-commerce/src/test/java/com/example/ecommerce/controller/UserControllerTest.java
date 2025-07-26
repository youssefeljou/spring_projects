package com.example.ecommerce.controller;

import com.example.ecommerce.service.customer.CustomerLoginService;
import com.example.ecommerce.service.user.CustomerAdminUserDetailsService;
import com.example.ecommerce.service.user.UserAuthService;
import com.example.ecommerce.service.user.UserLoginAttemptService;
import com.example.ecommerce.service.user.UserProfileService;
import com.example.ecommerce.session.AuthenticatedUserInfo;
import com.example.ecommerce.validation.LoginValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(UserControllerTest.MockConfig.class)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserProfileService userProfileService;

    // -------- Tests for POST /request-reset --------

    @Test
    void requestPasswordReset_ShouldReturn200AndSuccessMessage() throws Exception {
        String username = "john";

        doNothing().when(userProfileService).requestPasswordReset(username);

        mockMvc.perform(post("/api/users/request-reset")
                        .param("username", username))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset link sent to your email."));

        verify(userProfileService).requestPasswordReset(username);
    }

    @Test
    void requestPasswordReset_MissingUsername_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/users/request-reset"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requestPasswordReset_BlankUsername_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/api/users/request-reset")
                        .param("username", " "))
                .andExpect(status().isBadRequest());
    }

    // -------- Tests for PATCH /reset-password --------

    @Test
    void resetPassword_ShouldReturn200AndSuccessMessage() throws Exception {
        String token = "token123";
        String newPassword = "StrongPass123";

        doNothing().when(userProfileService).resetPassword(token, newPassword);

        String validJson = """
            {
                "token": "token123",
                "newPassword": "StrongPass123"
            }
        """;

        mockMvc.perform(patch("/api/users/reset-password")
                        .contentType("application/json")
                        .content(validJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Password has been reset successfully."));

        verify(userProfileService).resetPassword(token, newPassword);
    }

    @Test
    void resetPassword_MissingToken_ShouldReturn400() throws Exception {
        String jsonMissingToken = """
            {
                "newPassword": "StrongPass123"
            }
        """;

        mockMvc.perform(patch("/api/users/reset-password")
                        .contentType("application/json")
                        .content(jsonMissingToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resetPassword_BlankToken_ShouldReturn400() throws Exception {
        String jsonWithBlankToken = """
            {
                "token": " ",
                "newPassword": "StrongPass123"
            }
        """;

        mockMvc.perform(patch("/api/users/reset-password")
                        .contentType("application/json")
                        .content(jsonWithBlankToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resetPassword_MissingNewPassword_ShouldReturn400() throws Exception {
        String jsonMissingPassword = """
            {
                "token": "token123"
            }
        """;

        mockMvc.perform(patch("/api/users/reset-password")
                        .contentType("application/json")
                        .content(jsonMissingPassword))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resetPassword_BlankNewPassword_ShouldReturn400() throws Exception {
        String jsonWithBlankPassword = """
            {
                "token": "token123",
                "newPassword": " "
            }
        """;

        mockMvc.perform(patch("/api/users/reset-password")
                        .contentType("application/json")
                        .content(jsonWithBlankPassword))
                .andExpect(status().isBadRequest());
    }

    @TestConfiguration
    static class MockConfig {
        @Bean public UserProfileService userProfileService() {
            return Mockito.mock(UserProfileService.class);
        }

        @Bean public UserAuthService userAuthService() {
            return Mockito.mock(UserAuthService.class);
        }

        @Bean public UserLoginAttemptService userLoginAttemptService() {
            return Mockito.mock(UserLoginAttemptService.class);
        }

        @Bean public CustomerLoginService customerLoginService() {
            return Mockito.mock(CustomerLoginService.class);
        }

        @Bean public LoginValidator loginValidator() {
            return Mockito.mock(LoginValidator.class);
        }

        @Bean public AuthenticatedUserInfo authenticatedUserInfo() {
            return Mockito.mock(AuthenticatedUserInfo.class);
        }

        @Bean public CustomerAdminUserDetailsService customerAdminUserDetailsService() {
            return Mockito.mock(CustomerAdminUserDetailsService.class);
        }
    }
}
