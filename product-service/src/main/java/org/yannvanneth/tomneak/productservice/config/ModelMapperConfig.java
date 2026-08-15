package org.yannvanneth.tomneak.productservice.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ModelMapperConfig class for configuring ModelMapper bean.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Configuration
public class ModelMapperConfig {

    /**
     * Creates and configures the ModelMapper bean.
     *
     * @return ModelMapper instance configured with strict matching strategy and private field access.
     */
    @Bean
    public ModelMapper modelMapper() {

        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        return mapper;
    }
}
