package org.yannvanneth.tomneak.paymentservice;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Payment Service application.
 * Incorporates SAGA Pattern transaction management and CDC Outbox event publishing.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 * {@code @description} PaymentServiceApplication class for running Payment Service.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@SecurityRequirement(name = "BearerAuth")
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "BearerAuth", bearerFormat = "JWT")
public class PaymentServiceApplication {

    /**
     * Main method to start the Spring Boot Payment Service Application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
