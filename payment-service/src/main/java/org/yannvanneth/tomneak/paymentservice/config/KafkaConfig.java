package org.yannvanneth.tomneak.paymentservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * KafkaConfig configures Apache Kafka topics and producer settings for Payment Service.
 *
 * @author Yann Vanneth
 * @since 2026-08-09
 */
@Configuration
public class KafkaConfig {

    @Value("${app.kafka.topic:payment.events}")
    private String topicName;

    /**
     * Defines NewTopic bean for payment SAGA events.
     *
     * @return NewTopic instance
     */
    @Bean
    public NewTopic paymentEventsTopic() {
        return TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
