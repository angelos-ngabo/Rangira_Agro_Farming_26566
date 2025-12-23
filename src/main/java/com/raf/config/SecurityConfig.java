package com.raf.config;

import com.raf.service.AuthService;
import com.raf.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

@Lazy
private final AuthService authService;
private final JwtUtil jwtUtil;
private final CorsConfigurationSource corsConfigurationSource;

@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
return config.getAuthenticationManager();
}

@Bean
public JwtAuthenticationFilter jwtAuthenticationFilter() {
return new JwtAuthenticationFilter(jwtUtil, authService);
}

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
http
.csrf(AbstractHttpConfigurer::disable)
.cors(cors -> cors.configurationSource(corsConfigurationSource))
.authorizeHttpRequests(auth -> auth

.requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/api/**").permitAll()

.requestMatchers(
org.springframework.http.HttpMethod.POST,
"/api/newsletter/subscribe",
"/api/newsletter/unsubscribe",
"/api/contact/send",
"/api/contact"
).permitAll()
.requestMatchers(
"/api/auth/**",
"/api/locations/**",
"/api/files/**",
"/api/newsletter/**",
"/api/contact/**",
"/api-docs/**",
"/swagger-ui/**",
"/swagger-ui.html",
"/v3/api-docs/**",
"/actuator/health",
"/api/health",
"/favicon.ico",
"/robots.txt",
"/error"
).permitAll()

.requestMatchers(
org.springframework.http.HttpMethod.GET,
"/api/crop-types/**",
"/api/warehouses"
).permitAll()

.requestMatchers(
org.springframework.http.HttpMethod.GET,
"/api/inventory"
).permitAll()

.requestMatchers(
org.springframework.http.HttpMethod.GET,
"/api/inventories/**"
).authenticated()

.requestMatchers(
org.springframework.http.HttpMethod.GET,
"/api/public/search/**"
).permitAll()

.requestMatchers(
org.springframework.http.HttpMethod.GET,
"/api/dashboard/search/**"
).authenticated()
.anyRequest().authenticated()
)
.sessionManagement(session -> session
.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
.userDetailsService(authService)
.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

return http.build();
}
}

