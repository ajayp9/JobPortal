package com.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.JobRequest;
import com.backend.dto.response.JobResponse;
import com.backend.service.JobService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    /*
     * Employer can post a new job.
     *
     * POST /api/jobs/post
     */
    @PostMapping("/post")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobResponse> postJob(
            Authentication authentication,
            @Valid @RequestBody JobRequest request) {

        JobResponse response =
                jobService.postJob(
                        authentication.getName(),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /*
     * Public API to get all jobs.
     *
     * Optional query parameters:
     * city
     * niche
     * searchKeyword
     *
     * GET /api/jobs/getall
     * GET /api/jobs/getall?city=Pune
     * GET /api/jobs/getall?niche=Java
     * GET /api/jobs/getall?searchKeyword=developer
     */
    @GetMapping("/getall")
    public ResponseEntity<List<JobResponse>> getAllJobs(
            @RequestParam(required = false)
            String city,

            @RequestParam(required = false)
            String niche,

            @RequestParam(required = false)
            String searchKeyword) {

        List<JobResponse> jobs =
                jobService.getAllJobs(
                        city,
                        niche,
                        searchKeyword
                );

        return ResponseEntity.ok(jobs);
    }

    /*
     * Public API to get one job by ID.
     *
     * GET /api/jobs/get/{id}
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<JobResponse> getJobById(
            @PathVariable Long id) {

        JobResponse response =
                jobService.getJobById(id);

        return ResponseEntity.ok(response);
    }

    /*
     * Employer can view only their own jobs.
     *
     * GET /api/jobs/getmyjobs
     */
    @GetMapping("/getmyjobs")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<List<JobResponse>> getMyJobs(
            Authentication authentication) {

        List<JobResponse> jobs =
                jobService.getMyJobs(
                        authentication.getName()
                );

        return ResponseEntity.ok(jobs);
    }

    /*
     * Employer can delete only their own job.
     *
     * DELETE /api/jobs/delete/{id}
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<String> deleteJob(
            Authentication authentication,
            @PathVariable Long id) {

        jobService.deleteJob(
                authentication.getName(),
                id
        );

        return ResponseEntity.ok(
                "Job deleted successfully"
        );
    }
}