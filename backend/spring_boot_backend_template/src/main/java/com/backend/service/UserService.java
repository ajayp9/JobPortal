package com.backend.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.backend.dto.request.ChangePasswordRequest;
import com.backend.dto.request.RegisterRequest;
import com.backend.dto.request.UpdateProfileRequest;
import com.backend.dto.response.UserResponse;

public interface UserService {

    UserResponse register(
            RegisterRequest request,
            MultipartFile resume
    );

    // Get the currently authenticated user's profile.
    UserResponse getCurrentUser(
            String email
    );

    // Update the currently authenticated user's profile.
    UserResponse updateCurrentUser(
            String email,
            UpdateProfileRequest request
    );

    // Change the currently authenticated user's password.
    void changePassword(
            String email,
            ChangePasswordRequest request
    );

    // Stateless JWT logout does not require database changes.
    void logout();

    List<UserResponse> getAllUsers();

    UserResponse getUserById(
            Long id
    );

	boolean existsByEmail(String email);
}