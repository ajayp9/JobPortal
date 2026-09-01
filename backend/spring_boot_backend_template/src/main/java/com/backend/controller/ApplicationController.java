package com.backend.controller;

import com.backend.dto.response.ApplicationResponse;
import com.backend.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(
            ApplicationService applicationService
    ) {
        this.applicationService = applicationService;
    }

    @PostMapping(
            value = "/post/{jobId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApplicationResponse> postApplication(

            @PathVariable Long jobId,

            @RequestParam("name")
            String name,

            @RequestParam("email")
            String email,

            @RequestParam("phone")
            String phone,

            @RequestParam("address")
            String address,

            @RequestParam("coverLetter")
            String coverLetter,

            @RequestParam(
                    value = "resume",
                    required = false
            )
            MultipartFile resume,

            Authentication authentication
    ) {

        ApplicationResponse response =
                applicationService.applyForJob(
                        jobId,
                        name,
                        email,
                        phone,
                        address,
                        coverLetter,
                        resume,
                        authentication
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/jobseeker/getall")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<List<ApplicationResponse>>
    getJobSeekerApplications(
            Authentication authentication
    ) {

        List<ApplicationResponse> applications =
                applicationService.getMyApplications(
                        authentication
                );

        return ResponseEntity.ok(applications);
    }

    @GetMapping("/employer/getall")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<List<ApplicationResponse>>
    getEmployerApplications(
            Authentication authentication
    ) {

        List<ApplicationResponse> applications =
                applicationService.getEmployerApplications(
                        authentication
                );

        return ResponseEntity.ok(applications);
    }

    @DeleteMapping("/delete/{applicationId}")
    @PreAuthorize("hasAnyRole('JOB_SEEKER', 'EMPLOYER')")
    public ResponseEntity<String> deleteApplication(
            @PathVariable Long applicationId,
            Authentication authentication
    ) {

        applicationService.deleteApplication(
                applicationId,
                authentication
        );

        return ResponseEntity.ok(
                "Application deleted successfully"
        );
    }
}