package org.yannvanneth.tomneak.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.yannvanneth.tomneak.userservice.exception.NotFoundException;
import org.yannvanneth.tomneak.userservice.model.entity.Users;
import org.yannvanneth.tomneak.userservice.model.request.UserProfileRequest;
import org.yannvanneth.tomneak.userservice.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private Users user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = Users.builder()
                .id(userId)
                .fullName("John Doe")
                .email("john.doe@example.com")
                .imageUrl("http://example.com/image.jpg")
                .isActive(true)
                .build();
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Users> userPage = new PageImpl<>(Collections.singletonList(user));
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        List<Users> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getFullName());
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Users result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.getUserById(userId);
        });

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void updateUserById_WhenUserExists_ShouldUpdateAndReturnUser() {
        UserProfileRequest request = new UserProfileRequest("Jane Doe", "jane.doe@example.com", "http://example.com/jane.jpg");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        doAnswer(invocation -> {
            UserProfileRequest req = invocation.getArgument(0);
            Users dest = invocation.getArgument(1);
            dest.setFullName(req.fullName());
            dest.setEmail(req.email());
            dest.setImageUrl(req.imageUrl());
            return null;
        }).when(userMapper).map(any(UserProfileRequest.class), any(Users.class));

        when(userRepository.save(any(Users.class))).thenReturn(user);

        Users result = userService.updateUserById(userId, request);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getFullName());
        assertEquals("jane.doe@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).map(request, user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUserById_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        UserProfileRequest request = new UserProfileRequest("Jane Doe", "jane.doe@example.com", "http://example.com/jane.jpg");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            userService.updateUserById(userId, request);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, never()).map(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUserById_WhenUserExists_ShouldDeleteUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(userId);

        assertDoesNotThrow(() -> userService.deleteUserById(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUserById_WhenUserDoesNotExist_ShouldThrowNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            userService.deleteUserById(userId);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).deleteById(any());
    }
}
