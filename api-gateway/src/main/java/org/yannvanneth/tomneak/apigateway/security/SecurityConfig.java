package org.yannvanneth.tomneak.apigateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * SecurityConfig configures Spring Security WebFlux filter chains, OAuth2 Resource Server JWT validation,
 * public path permissions, and custom entry points for API Gateway.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtConverter jwtConverter;
    private final JwtEntryPoint jwtEntryPoint;

    /**
     * Configures the SecurityWebFilterChain for Spring Cloud API Gateway HTTP exchanges.
     *
     * @param http ServerHttpSecurity builder instance
     * @return SecurityWebFilterChain instance for WebFlux
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                // Disable CSRF for stateless REST microservice communication
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // Configure request path authorization rules
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/products/**", "/api/v1/categories/**").permitAll()
                        .anyExchange().authenticated()
                )
                // Configure OAuth2 JWT Resource Server with custom JwtConverter
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter))
                        .authenticationEntryPoint(jwtEntryPoint)
                )
                // Exception handling entry point for unauthenticated requests
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jwtEntryPoint)
                )
                .build();
    }
}
