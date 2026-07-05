package org.yannvanneth.tomneak.userservice.model.response;

import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String fullName,
        String email,
        String role,
        String imageUrl,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
}
