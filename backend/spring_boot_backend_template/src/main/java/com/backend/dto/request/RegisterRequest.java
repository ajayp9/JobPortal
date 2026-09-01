package com.backend.dto.request;

import com.backend.entities.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(
            min = 3,
            max = 30,
            message = "Name must be between 3 and 30 characters"
    )
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Password is required")
    @Size(
            min = 8,
            max = 32,
            message = "Password must be between 8 and 32 characters"
    )
    private String password;

    private String coverLetter;

    @NotNull(message = "Role is required")
    private Role role;

    @NotBlank(message = "First niche is required")
    private String firstNiche;

    @NotBlank(message = "Second niche is required")
    private String secondNiche;

    @NotBlank(message = "Third niche is required")
    private String thirdNiche;
}