package org.yannvanneth.tomneak.keycloak;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Event listener implementation that intercepts Keycloak user lifecycle events 
 * (registration, updates, deletions) and publishes them to a Kafka broker.
 *
 * @author Yann Vanneth
 * @since 2026-07-05
 */
public class KafkaEventListenerProvider implements EventListenerProvider {

    private static final Logger logger = Logger.getLogger(KafkaEventListenerProvider.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final KeycloakSession session;
    private final KafkaProducer<String, String> producer;
    private final String topicName;

    /**
     * Constructs a new {@link KafkaEventListenerProvider}.
     *
     * @param session   the current Keycloak session
     * @param producer  the shared Kafka producer instance
     * @param topicName the target Kafka topic for synchronization
     */
    public KafkaEventListenerProvider(KeycloakSession session, KafkaProducer<String, String> producer, String topicName) {
        this.session = session;
        this.producer = producer;
        this.topicName = topicName;
    }

    /**
     * Intercepts standard user actions (e.g., REGISTER, UPDATE_PROFILE, UPDATE_EMAIL).
     *
     * @param event the standard Keycloak event
     */
    @Override
    public void onEvent(Event event) {
        logger.info(String.format("Received event: %s", event.getType()));
        String type = event.getType().name();
        if (type.equals("REGISTER")) {
            handleUserEvent("CREATE", event.getUserId());
        } else if (type.equals("UPDATE_PROFILE") || type.equals("UPDATE_EMAIL")) {
            handleUserEvent("UPDATE", event.getUserId());
        }
    }

    /**
     * Intercepts admin activities, specifically user additions, modifications, and deletions.
     *
     * @param adminEvent            the Keycloak admin event
     * @param includeRepresentation whether to include representation payload details
     */
    @Override
    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
        logger.info(String.format("Received admin event: %s %s", adminEvent.getOperationType(), adminEvent.getResourceType()));
        if (adminEvent.getResourceType() == ResourceType.USER) {
            String resourcePath = adminEvent.getResourcePath();
            if (resourcePath != null && resourcePath.startsWith("users/")) {
                String userId = resourcePath.substring(6);
                OperationType operation = adminEvent.getOperationType();
                if (operation == OperationType.CREATE) {
                    handleUserEvent("CREATE", userId);
                } else if (operation == OperationType.UPDATE) {
                    handleUserEvent("UPDATE", userId);
                } else if (operation == OperationType.DELETE) {
                    handleDeleteEvent(userId);
                }
            }
        }
    }

    /**
     * Resolves user details from Keycloak session database and publishes a CREATE or UPDATE event payload.
     *
     * @param action the event action category (CREATE, UPDATE)
     * @param userId the Keycloak user identifier
     */
    private void handleUserEvent(String action, String userId) {
        try {
            RealmModel realm = session.getContext().getRealm();
            UserModel user = session.users().getUserById(realm, userId);
            if (user == null) {
                logger.warning(String.format("User not found in Keycloak: %s", userId));
                return;
            }

            Map<String, Object> message = new HashMap<>();
            message.put("action", action);
            message.put("keycloakId", userId);
            message.put("username", user.getUsername());
            message.put("email", user.getEmail());
            message.put("firstName", user.getFirstName());
            message.put("lastName", user.getLastName());

            publishMessage(userId, message);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing user event: " + action, e);
        }
    }

    /**
     * Publishes a DELETE event payload containing the deleted user's Keycloak ID.
     *
     * @param userId the Keycloak user identifier
     */
    private void handleDeleteEvent(String userId) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("action", "DELETE");
            message.put("keycloakId", userId);

            publishMessage(userId, message);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing user delete event", e);
        }
    }

    /**
     * Serializes the message map to JSON and publishes it to the Kafka topic.
     *
     * @param key     the partition key (Keycloak User ID)
     * @param message the message map payload
     */
    private void publishMessage(String key, Map<String, Object> message) {
        if (producer == null) {
            logger.severe("Kafka Producer is not initialized. Cannot publish message.");
            return;
        }

        try {
            String jsonPayload = objectMapper.writeValueAsString(message);
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, key, jsonPayload);
            
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    logger.log(Level.SEVERE, "Failed to send message to Kafka topic " + topicName, exception);
                } else {
                    logger.info(String.format("Successfully published user sync message to Kafka topic '%s' offset %d: %s",
                            metadata.topic(), metadata.offset(), jsonPayload));
                }
            });
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during message serialization or Kafka publishing", e);
        }
    }

    /**
     * Cleans up any resources held by the provider session.
     */
    @Override
    public void close() {
        // Managed by factory
    }
}
