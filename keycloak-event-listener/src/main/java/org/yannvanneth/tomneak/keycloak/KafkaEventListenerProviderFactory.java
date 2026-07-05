package org.yannvanneth.tomneak.keycloak;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory implementation for the Keycloak Kafka Event Listener.
 * Responsible for configuring, instantiating, and managing the lifecycle of
 * the Kafka producer used by the event listener.
 *
 * @author Yann Vanneth
 * @since 2026-07-05
 */
public class KafkaEventListenerProviderFactory implements EventListenerProviderFactory {

    private static final Logger logger = Logger.getLogger(KafkaEventListenerProviderFactory.class.getName());
    
    /**
     * Unique identifier for the provider registration in Keycloak.
     */
    public static final String PROVIDER_ID = "kafka-event-listener";

    private KafkaProducer<String, String> producer;
    private String topicName;

    /**
     * Creates a new instance of {@link KafkaEventListenerProvider}.
     *
     * @param session the current Keycloak session
     * @return the event listener provider instance
     */
    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new KafkaEventListenerProvider(session, producer, topicName);
    }

    /**
     * Initializes the Kafka producer and loads configuration from environment variables.
     * Sets up properties including bootstrap servers, serialization classes, and idempotence.
     *
     * @param config the scope configuration from Keycloak
     */
    @Override
    public void init(Config.Scope config) {
        logger.info("Initializing Kafka Event Listener Factory...");

        String bootstrapServers = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
        topicName = System.getenv().getOrDefault("KAFKA_TOPIC", "user.registration");

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        // Idempotent producer configurations for reliability
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

        try {
            this.producer = new KafkaProducer<>(props);
            logger.info("Successfully initialized Kafka Producer with brokers: " + bootstrapServers);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize Kafka Producer", e);
        }
    }

    /**
     * Performs post-initialization logic.
     *
     * @param factory the Keycloak session factory
     */
    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    /**
     * Closes the Kafka producer and cleans up resources on shutdown.
     */
    @Override
    public void close() {
        if (producer != null) {
            try {
                producer.close();
                logger.info("Closed Kafka Producer.");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error closing Kafka Producer", e);
            }
        }
    }

    /**
     * Returns the unique provider ID.
     *
     * @return the provider ID string
     */
    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
