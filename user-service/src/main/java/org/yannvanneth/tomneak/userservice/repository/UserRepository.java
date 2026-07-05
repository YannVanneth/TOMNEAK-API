package org.yannvanneth.tomneak.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yannvanneth.tomneak.userservice.model.entity.Users;

import java.util.Optional;
import java.util.UUID;

/**
 * UserRepository class for interacting with the user database.
 * @since 2026-06-21
 * */
@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByKeycloakId(UUID keycloakId);
    boolean existsByKeycloakId(UUID keycloakId);
    boolean existsByEmail(String email);
}
