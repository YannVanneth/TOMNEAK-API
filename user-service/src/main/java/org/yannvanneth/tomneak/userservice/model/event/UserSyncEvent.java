package org.yannvanneth.tomneak.userservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSyncEvent {
    private String action; // CREATE, UPDATE, DELETE
    private UUID keycloakId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
}
