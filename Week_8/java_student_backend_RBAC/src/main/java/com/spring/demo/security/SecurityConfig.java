package com.spring.demo.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // This tells Spring Security:
    // Use CustomUserDetailsManager to look up users.
    // Use BCrypt to verify passwords.
    // Return the configured provider to Spring's application context.
    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            CustomUserDetailsManager userDetailsManager,
            BCryptPasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider auth =
                new DaoAuthenticationProvider(userDetailsManager);

        auth.setPasswordEncoder(passwordEncoder);
        return auth;
    }

    // defines how spring security authenticates users
    // our AuthService uses this
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // defines our global CORS settings, allowed methods, and AllowCredentials supports HttpOnly cookies
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://localhost:3000",
                "https://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Content-Type", "X-XSRF-TOKEN", "Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // this filter chain determines which routes are protected 
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {
                // hasRole() always expects a name minus the default prefix 'ROLE_'
                // i.e. hasRole("ADMIN") = "ROLE_ADMIN" saved in db
        http.authorizeHttpRequests(configurer -> configurer
                .requestMatchers(HttpMethod.GET, "/api/students")
                    .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/students/**")
                    .permitAll()
                .requestMatchers(HttpMethod.POST, "/api/students")
                    .hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/students/**")
                    .hasAnyRole("MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/students/**")
                    .hasRole("ADMIN")
            // AS-IS ANYONE CAN ASSIGN ROLES - DEMO AND EASE OF USE PURPOSES ONLY
            // AFTER WE CREATE AN ADMIN, YOU CAN CHANGE THIS SO THAT THE 
            // ASSIGN ROLE ROUTE IS ONLY AVAILABLE FOR ADMINS
                .requestMatchers(
                        "/api/auth/register",
                        "/api/auth/login",
                        "/api/auth/logout",
                        "/api/auth/csrf",
                        "/api/auth/assign-role")
                    .permitAll()
                .requestMatchers("/api/auth/**")
                    .authenticated()

                .anyRequest().authenticated()
        );

        // this ties everything together before building our configurations 
        // the csrfTokenRepositry is built-in spring security, and it provides
        // csrf tokens to the client - this is a defense against csrf attacks 
        AuthenticationEntryPoint apiAuthenticationEntryPoint =
            (request, response, exception) -> response.setStatus(401);
        http.httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(apiAuthenticationEntryPoint));
        http.cors(Customizer.withDefaults());
        http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));

        return http.build();
    }
}
