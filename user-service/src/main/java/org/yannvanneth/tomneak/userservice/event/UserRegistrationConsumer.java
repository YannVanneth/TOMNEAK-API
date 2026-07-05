package org.yannvanneth.tomneak.userservice.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.yannvanneth.tomneak.userservice.model.event.UserSyncEvent;
import org.yannvanneth.tomneak.userservice.service.UserService;

/**
 * UserRegistrationConsumer class for consuming user registration events from Kafka.
 * Responsible for handling user registration events and updating the user database accordingly.
 * @since 2026-07-05
 * */

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegistrationConsumer {

    // Dependency Injection
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserRegistration(String message) {
        log.info("Received UserSync message from Kafka: {}", message);
        try {
            UserSyncEvent event = objectMapper.readValue(message, UserSyncEvent.class);
            log.info("Parsed event: {}", event);

            if ("DELETE".equalsIgnoreCase(event.getAction())) {
                userService.deleteUser(event);
                log.info("Successfully deleted user: {}", event.getKeycloakId());
            } else if ("UPDATE".equalsIgnoreCase(event.getAction())) {
                userService.updateUser(event);
                log.info("Successfully updated user: {}", event.getKeycloakId());
            } else {
                // Default to CREATE / REGISTER
                userService.registerUser(event);
                log.info("Successfully registered user: {}", event.getKeycloakId());
            }
        } catch (Exception e) {
            log.error("Failed to process user sync message: {}", message, e);
        }
    }
}
