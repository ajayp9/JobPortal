package com.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {

    @NotBlank(message = "Name is required")
    @Size(
            min = 3,
            max = 30,
            message = "Name must be between 3 and 30 characters"
    )
    private String name;

    @Pattern(
            regexp = "^[6-9][0-9]{9}$",
            message = "Phone number must be exactly 10 digits and start with 6, 7, 8, or 9"
    )
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    private String coverLetter;

    private String firstNiche;

    private String secondNiche;

    private String thirdNiche;
}