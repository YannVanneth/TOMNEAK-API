package org.yannvanneth.tomneak.userservice.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yannvanneth.tomneak.userservice.exception.NotFoundException;
import org.yannvanneth.tomneak.userservice.model.entity.Users;
import org.yannvanneth.tomneak.userservice.model.request.UserProfileRequest;
import org.yannvanneth.tomneak.userservice.model.event.UserSyncEvent;
import org.yannvanneth.tomneak.userservice.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


/**
 * This class is responsible for providing user-related services.
 * @author Yann Vanneth
 * @since 2026-06-21
 */

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    // Dependency Injection here
    private final UserRepository userRepository;
    private final ModelMapper userMapper;

    /**
     * Retrieves all users
     * @param pageable use for pagination metadata
     * @return List<Users>
     */
    @Override
    public List<Users> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).getContent();
    }

    /**
     * Retrieves user by id
     * @param id use for retrieving user by id
     * @return Users
     * @throws NotFoundException if a user is not found
     **/
    @Override
    public Users getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("User not found with id: " + id));
    }

    /**
     * Updates user by id
     * @param id use for updating user by id
     * @param user use for updating user
     * @return Users
     **/
    @Transactional
    @Override
    public Users updateUserById(UUID id, UserProfileRequest user) {

        Users updateUser = userRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("User not found with id: " + id));

        userMapper.map(user, updateUser);
        userRepository.save(updateUser);

        return updateUser;
    }

    /**
     * Delete user by id
     * @param id use for deleting user by id
     * @throws NotFoundException if a user is not found
     **/
    @Override
    public void deleteUserById(UUID id) {
        userRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("User not found with id: " + id));
        userRepository.deleteById(id);
    }

    /**
     * Registers a new user consumed from Kafka
     * @param event containing user details from Keycloak
     */
    @Transactional
    @Override
    public void registerUser(UserSyncEvent event) {
        if (event.getKeycloakId() == null) {
            throw new IllegalArgumentException("Keycloak ID cannot be null");
        }

        // Check if user already exists
        if (userRepository.existsByKeycloakId(event.getKeycloakId())) {
            return; // Idempotence
        }

        // Check if email already exists, if so return to avoid duplicate email exceptions
        if (event.getEmail() != null && userRepository.existsByEmail(event.getEmail())) {
            return;
        }

        String fullName = "";
        if (event.getFirstName() != null && !event.getFirstName().isBlank()) {
            fullName += event.getFirstName();
        }
        if (event.getLastName() != null && !event.getLastName().isBlank()) {
            if (!fullName.isEmpty()) {
                fullName += " ";
            }
            fullName += event.getLastName();
        }
        if (fullName.isEmpty()) {
            fullName = event.getUsername() != null ? event.getUsername() : "User";
        }

        Users user = Users.builder()
                .keycloakId(event.getKeycloakId())
                .email(event.getEmail() != null ? event.getEmail() : "no-email-" + event.getKeycloakId() + "@tomneak.com")
                .fullName(fullName)
                .role(event.getRole() != null ? event.getRole() : "ROLE_USER")
                .isActive(true)
                .createdAt(Instant.now())
                .build();

        userRepository.save(user);
    }

    /**
     * Updates an existing user consumed from Kafka
     * @param event containing updated user details from Keycloak
     */
    @Transactional
    @Override
    public void updateUser(UserSyncEvent event) {
        if (event.getKeycloakId() == null) {
            return;
        }
        userRepository.findByKeycloakId(event.getKeycloakId()).ifPresent(user -> {
            String fullName = "";
            if (event.getFirstName() != null && !event.getFirstName().isBlank()) {
                fullName += event.getFirstName();
            }
            if (event.getLastName() != null && !event.getLastName().isBlank()) {
                if (!fullName.isEmpty()) {
                    fullName += " ";
                }
                fullName += event.getLastName();
            }
            if (fullName.isEmpty()) {
                fullName = event.getUsername() != null ? event.getUsername() : user.getFullName();
            }

            user.setFullName(fullName);
            if (event.getEmail() != null) {
                user.setEmail(event.getEmail());
            }
            if (event.getRole() != null) {
                user.setRole(event.getRole());
            }
            userRepository.save(user);
        });
    }

    /**
     * Deletes an existing user consumed from Kafka
     * @param event containing keycloakId of the user to delete
     */
    @Transactional
    @Override
    public void deleteUser(UserSyncEvent event) {
        if (event.getKeycloakId() == null) {
            return;
        }
        userRepository.findByKeycloakId(event.getKeycloakId()).ifPresent(user -> {
            userRepository.delete(user);
        });
    }
}
