package org.yannvanneth.tomneak.productservice;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main entry point for the Product Service application.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 * {@code @description} ProductServiceApplication class for running Product Service.
 */
@SpringBootApplication
@EnableDiscoveryClient
@SecurityRequirement(name = "BearerAuth")
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "BearerAuth", bearerFormat = "JWT")
public class ProductServiceApplication {

    /**
     * Main method to start the Spring Boot Application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
