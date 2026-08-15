package org.yannvanneth.tomneak.apigateway;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main entry point for the API Gateway application.
 * Acts as the centralized entry point for request routing, security, and load balancing across microservices.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 * {@code @description} ApiGatewayApplication class for running Spring Cloud API Gateway.
 */
@SpringBootApplication
@EnableDiscoveryClient
@SecurityRequirement(name = "BearerAuth")
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "BearerAuth", bearerFormat = "JWT")
public class ApiGatewayApplication {

    /**
     * Main method to start the Spring Cloud API Gateway Application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
