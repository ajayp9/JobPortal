package com.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.backend.entities.HiringMultipleCandidates;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JobResponse {

    private Long id;

    private String title;

    private String jobType;

    private String location;

    private String companyName;

    private String introduction;

    private String responsibilities;

    private String qualifications;

    private String offers;

    private BigDecimal salary;

    private HiringMultipleCandidates hiringMultipleCandidates;

    private String jobNiche;

    private Boolean newslettersSent;

    // Employer details
    private Long employerId;

    private String employerName;

    private String employerEmail;

    // Optional personal websites
    private List<PersonalWebsiteResponse> personalWebsites;

    private LocalDateTime jobPostedOn;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}