package com.backend.service;

import java.util.List;

import com.backend.dto.request.JobRequest;
import com.backend.dto.response.JobResponse;

public interface JobService {

    JobResponse postJob(
            String employerEmail,
            JobRequest request
    );

    List<JobResponse> getAllJobs(
            String city,
            String niche,
            String searchKeyword
    );

    JobResponse getJobById(
            Long jobId
    );

    List<JobResponse> getMyJobs(
            String employerEmail
    );

    void deleteJob(
            String employerEmail,
            Long jobId
    );
}