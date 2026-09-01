package com.backend.dto.request;

import java.math.BigDecimal;

import com.backend.entities.HiringMultipleCandidates;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JobRequest {

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Job type is required")
    private String jobType;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Introduction is required")
    private String introduction;

    @NotBlank(message = "Responsibilities are required")
    private String responsibilities;

    @NotBlank(message = "Qualifications are required")
    private String qualifications;

    private String offers;

    @NotNull(message = "Salary is required")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Salary must be greater than zero"
    )
    private BigDecimal salary;

    private HiringMultipleCandidates hiringMultipleCandidates;

    @NotBlank(message = "Job niche is required")
    private String jobNiche;

    /*
     * Both website fields are optional.
     * If one is supplied, the other should also be supplied.
     */
    private String personalWebsiteTitle;

    private String personalWebsiteUrl;
}