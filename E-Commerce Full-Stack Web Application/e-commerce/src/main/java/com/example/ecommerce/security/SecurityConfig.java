package com.example.ecommerce.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().changeSessionId()
                ).authorizeHttpRequests(authorize -> authorize
                        // ✅ Public endpoints (permitAll)

                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/**",
                                "/csrf-token"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST,

                                "/api/users/login",
                                "/api/users/logout",
                                "/api/customers/register",
                                "/api/customers/verify",

                                "/api/users/request-reset"
                        ).permitAll()



                        .requestMatchers(HttpMethod.GET,
                                "/api/products/search",
                                "/api/products/filter",
                                "/api/products/*/reviews",
                                "/api/products/*/reviews/average-rating",
                                "/api/customers/{customerId}/cards",
                                "/api/customers/{customerId}/cards/{cardId}"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST,
                                "/api/customers/{customerId}/cards"
                        ).permitAll()

                        .requestMatchers(HttpMethod.PUT,
                                "/api/customers/{customerId}/cards/{cardId}"
                        ).permitAll()

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/customers/{customerId}/cards/{cardId}"
                        ).permitAll()

                        // ✅ Role-based access
                        .requestMatchers(HttpMethod.GET,
                        "/api/customers/*/addresses",
                        "/api/customers/*/addresses/*"
                        ).hasRole("CUSTOMER")

                        .requestMatchers(HttpMethod.POST,
                                "/api/products/*/reviews/add",
                                "/api/cart/add-to-cart",
                                "/api/cart/update-cart",
                                "/api/cart/remove-item",
                                "/api/cart/clear-cart",
                                "/api/customers/*/addresses"
                        ).hasRole("CUSTOMER")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/products/*/reviews/update",
                                "/api/customers/*/addresses/*"

                        ).hasRole("CUSTOMER")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/customers/*/addresses/*"
                        ).hasRole("CUSTOMER")

                        .requestMatchers(HttpMethod.POST,
                                "/api/admin/add"
                        ).hasRole("SUPERADMIN")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/admin/delete/{id}"
                        ).hasRole("SUPERADMIN")

                        .requestMatchers(HttpMethod.POST,
                                "/api/admin/items/add"
                        ).hasAnyRole("ADMIN", "SUPERADMIN")

                        .requestMatchers(HttpMethod.POST,
                                "/api/admin/update/{id}"
                        ).hasRole("SUPERADMIN")

                        // ✅ Catch-all: secured by default

                        .anyRequest().authenticated()
                )
                .formLogin(withDefaults())
                .httpBasic(withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
