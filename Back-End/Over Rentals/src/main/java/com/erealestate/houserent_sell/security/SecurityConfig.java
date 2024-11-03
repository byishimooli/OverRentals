package com.erealestate.houserent_sell.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf
                                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                                // cookies
                                // for
                                // CSRF
                                )
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Change as
                                                                                                          // necessary
                                                .maximumSessions(1)
                                                .sessionRegistry(sessionRegistry()))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/api/users/signup",
                                                                "/api/users/login",
                                                                "/api/users/all",
                                                                "/auth/forgot-password",
                                                                "/auth/reset-password",
                                                                "/api/users/**",
                                                                "/api/users/delete/**",
                                                                "/auth/**",
                                                                "/code/**",
                                                                "/public/**",
                                                                "/properties/post",
                                                                "/api/properties/post",
                                                                "/api/properties/upload",
                                                                "/api/properties") // Allow access to fetch properties
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .build();
        }

        @Bean
        public SessionRegistry sessionRegistry() {
                return new SessionRegistryImpl();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Arrays.asList("*")); // In production, replace with specific origins
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setExposedHeaders(Arrays.asList("Authorization", "X-CSRF-TOKEN")); // Expose CSRF token
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
