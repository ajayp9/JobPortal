package com.backend.controller;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.ChangePasswordRequest;
import com.backend.dto.request.UpdateProfileRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.UserResponse;
import com.backend.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /*
     * Get the authenticated user's profile.
     */
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(
            Authentication authentication) {

        String email = authentication.getName();

        UserResponse response =
                userService.getCurrentUser(email);

        return ResponseEntity.ok(response);
    }

    /*
     * Update the authenticated user's profile.
     */
    @PutMapping(
            value = "/profile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserResponse> updateProfile(
            Authentication authentication,
         @Valid @ModelAttribute UpdateProfileRequest request) {

        String email = authentication.getName();

        UserResponse response =
                userService.updateCurrentUser(
                        email,
                        request
                );

        return ResponseEntity.ok(response);
    }

    /*
     * Change the authenticated user's password.
     */
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        String email = authentication.getName();

        userService.changePassword(
                email,
                request
        );

        return ResponseEntity.ok(
                new ApiResponse(
                        "Password changed successfully"
                )
        );
    }

    /*
     * Stateless JWT logout.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout() {

        userService.logout();

        return ResponseEntity.ok(
                new ApiResponse(
                        "Logged out successfully"
                )
        );
    }

    /*
     * Existing employer-only endpoint.
     */
    @GetMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }

    /*
     * Existing employer-only endpoint.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                userService.getUserById(id)
        );
    }
}