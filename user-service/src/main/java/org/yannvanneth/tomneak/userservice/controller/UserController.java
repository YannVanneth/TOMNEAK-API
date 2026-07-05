package org.yannvanneth.tomneak.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yannvanneth.tomneak.userservice.model.entity.Users;
import org.yannvanneth.tomneak.userservice.model.request.UserProfileRequest;
import org.yannvanneth.tomneak.userservice.model.response.ApiResponse;
import org.yannvanneth.tomneak.userservice.model.response.ApiResponseFactory;
import org.yannvanneth.tomneak.userservice.service.UserService;

import java.util.List;
import java.util.UUID;

/**
 * @author Yann Vanneth
 * @since 2026-06-21
 * {@code @description} UserController class for handling user-related requests.
 */

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    // Dependency Injection here
    private final UserService userService;
    private final ApiResponseFactory responseFactory;

    /**
     * Retrieves user profiles
     * @param page use for pagination which page to retrieve and has default value 1
     * @param size use for pagination which sizes to retrieve per page and has default value 10
     * @return ResponseEntity<Users>
     * */
    @GetMapping
    public ResponseEntity<List<Users>> getUsers(@RequestParam(required = false, defaultValue = "1") Integer page,
                                                @RequestParam(required = false, defaultValue = "10") Integer size){

        List<Users> users = userService.getAllUsers(Pageable.ofSize(size).withPage(page - 1));

        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves user profile by id
     * @param id use for retrieving user profile by id
     * @return ResponseEntity<Users>
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Users> getUserById(@PathVariable("userId") UUID id){
        Users user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }


    /**
     * Update user profile
     * @param id use for updating user profile by id
     * @param user use for updating user profile
     * @return ResponseEntity<Users>
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<Users>> updateUser(@PathVariable("userId") UUID id, @RequestBody UserProfileRequest user){
        ApiResponse<Users> response = responseFactory.success(userService.updateUserById(id, user), "Update user successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Delete user profile
     * @param id use for deleting user profile by id
     * @return ResponseEntity<ApiResponse<Void>>
     **/
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("userId") UUID id){
        userService.deleteUserById(id);
        return ResponseEntity.ok(responseFactory.success(null, "Delete user successfully"));
    }
}
