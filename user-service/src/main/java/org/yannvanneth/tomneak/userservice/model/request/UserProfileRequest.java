package org.yannvanneth.tomneak.userservice.model.request;

public record UserProfileRequest(
        String fullName,
        String email,
        String imageUrl
) {}
