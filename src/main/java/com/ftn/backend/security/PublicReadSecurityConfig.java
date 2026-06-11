package com.ftn.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

/**
 * Permits unauthenticated GET access to all public read-only API endpoints.
 * Runs before SecurityConfig (Order=1 < SecurityConfig's default order).
 */
@Configuration
public class PublicReadSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain publicReadFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/api/v1/pools/**", "GET"),
                        new AntPathRequestMatcher("/api/v1/pool-schedules/**", "GET"),
                        new AntPathRequestMatcher("/api/v1/athletes/**", "GET"),
                        new AntPathRequestMatcher("/api/v1/clubs/**", "GET"),
                        new AntPathRequestMatcher("/api/v1/competitions/**", "GET"),
                        new AntPathRequestMatcher("/api/v1/events/**", "GET"),
                        new AntPathRequestMatcher("/api/v1/results/**", "GET"),
                        new AntPathRequestMatcher("/api/v1/rankings/**", "GET"),
                        new AntPathRequestMatcher("/api/v1/records/**", "GET"),
                        new AntPathRequestMatcher("/api/v1/licences/**", "GET"),
                        new AntPathRequestMatcher("/api/v1/actualites/**", "GET"),
                        new AntPathRequestMatcher("/api/v1/forums/**", "GET"),
                        new AntPathRequestMatcher("/api/v1/sujets/**", "GET"),
                        new AntPathRequestMatcher("/api/v1/reponses/**", "GET"),
                        new AntPathRequestMatcher("/api/v1/forum-categories/**", "GET"),
                        new AntPathRequestMatcher("/api/v1/threads/**", "GET"),
                        new AntPathRequestMatcher("/api/v1/posts/**", "GET")))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
