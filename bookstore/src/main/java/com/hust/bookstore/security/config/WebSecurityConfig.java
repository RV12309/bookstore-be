package com.hust.bookstore.security.config;

import com.hust.bookstore.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.hust.bookstore.common.Constants.*;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    public static final String[] NOT_NEAD_AUTH_ENDPOINT = {"/actuator/**", "/swagger-ui/**", "/v3/api-docs",
            "/v1/auth/login", "/v1/users/register", "/v1/users/confirm",
            "/v1/accounts/forgot-password",
            "/v1/users/reset-password", "/v1/accounts/verification",
            "/v3/api-docs/swagger-config", "/v1/ping", "/v1/home/**",
            "/v1/customers/register", "/v1/sellers/register",
            "/v1/books/**", "/v1/categories/all", "/v1/shopping-cart/**", "/v1/delivery/**", "/v1/orders/**"};

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(NOT_NEAD_AUTH_ENDPOINT)
                        .permitAll()
                        .requestMatchers("/v1/sellers/**").hasAnyRole(List.of(ADMIN_ROLE, SELLER_ROLE)
                                .toArray(String[]::new))
                        .requestMatchers("/v1/admin/**").hasRole(ADMIN_ROLE)
                        .requestMatchers("/v1/users/**").hasAnyRole(List.of(ADMIN_ROLE, SELLER_ROLE, CUSTOMER_ROLE)
                                .toArray(String[]::new))
                        .requestMatchers("/v1/customers/**").hasAnyRole(List.of(ADMIN_ROLE, SELLER_ROLE, CUSTOMER_ROLE)
                                .toArray(String[]::new))
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(userDetailsService)
                .build();

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
