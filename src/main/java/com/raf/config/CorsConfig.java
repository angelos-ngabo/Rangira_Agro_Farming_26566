package com.raf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Configuration
public class CorsConfig {

@Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173,http://localhost:4200}")
private String allowedOriginsCsv;

@Bean
public CorsConfigurationSource corsConfigurationSource() {
UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
CorsConfiguration config = new CorsConfiguration();


config.setAllowCredentials(true);


List<String> origins = Stream.of(allowedOriginsCsv.split(","))
.map(String::trim)
.filter(s -> !s.isEmpty())
.toList();
config.setAllowedOrigins(origins);


config.setAllowedHeaders(Arrays.asList(
"Origin", "Content-Type", "Accept", "Authorization",
"X-Requested-With", "Access-Control-Request-Method",
"Access-Control-Request-Headers"
));


config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));


config.setExposedHeaders(Arrays.asList(
"Authorization", "Content-Type", "X-Total-Count"
));


config.setMaxAge(3600L);

source.registerCorsConfiguration("/api/**", config);
return source;
}

@Bean
public CorsFilter corsFilter() {
return new CorsFilter(corsConfigurationSource());
}
}

