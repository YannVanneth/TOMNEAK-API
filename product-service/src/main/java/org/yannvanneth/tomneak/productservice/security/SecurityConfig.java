package org.yannvanneth.tomneak.productservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig configures Spring Security filter chains and OAuth2 Resource Server JWT protection.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtConverter jwtConverter;
    private final JwtEntryPoint jwtEntryPoint;

    /**
     * Configures the SecurityFilterChain for Product Service HTTP endpoints.
     *
     * @param http HttpSecurity builder
     * @return SecurityFilterChain instance
     * @throws Exception in case of configuration errors
     */
    @Bean
    public SecurityFilterChain securityFilterConfig(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationRequests -> authorizationRequests
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**", "/api/v1/categories/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer ->
                        jwtConfigurer.jwtAuthenticationConverter(jwtConverter)))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jwtEntryPoint))
                .formLogin(AbstractHttpConfigurer::disable)
                .build();
    }
}
