package org.yannvanneth.tomneak.paymentservice.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ModelMapperConfig provides bean definition for object mapping between entities and DTOs.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Configuration
public class ModelMapperConfig {

    /**
     * Instantiates ModelMapper bean.
     *
     * @return ModelMapper instance
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
