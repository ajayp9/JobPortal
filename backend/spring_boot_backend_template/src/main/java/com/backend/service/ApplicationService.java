package com.backend.service;

import com.backend.dto.response.ApplicationResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ApplicationService {

    ApplicationResponse applyForJob(
            Long jobId,
            String name,
            String email,
            String phone,
            String address,
            String coverLetter,
            MultipartFile resume,
            Authentication authentication
    );

    List<ApplicationResponse> getMyApplications(
            Authentication authentication
    );

    List<ApplicationResponse> getEmployerApplications(
            Authentication authentication
    );

    void deleteApplication(
            Long applicationId,
            Authentication authentication
    );
}