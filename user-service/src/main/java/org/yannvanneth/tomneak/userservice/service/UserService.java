package org.yannvanneth.tomneak.userservice.service;

import org.springframework.data.domain.Pageable;
import org.yannvanneth.tomneak.userservice.model.entity.Users;
import org.yannvanneth.tomneak.userservice.model.request.UserProfileRequest;
import org.yannvanneth.tomneak.userservice.model.event.UserSyncEvent;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<Users> getAllUsers(Pageable pageable);
    Users getUserById(UUID id);
    Users updateUserById(UUID id, UserProfileRequest user);
    void deleteUserById(UUID id);
    void registerUser(UserSyncEvent event);
    void updateUser(UserSyncEvent event);
    void deleteUser(UserSyncEvent event);
}