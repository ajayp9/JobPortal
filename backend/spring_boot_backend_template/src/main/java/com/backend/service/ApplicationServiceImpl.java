package com.backend.service;

import com.backend.dto.response.ApplicationResponse;
import com.backend.entities.Application;
import com.backend.entities.ApplicationDeletedBy;
import com.backend.entities.ApplicationEmployerInfo;
import com.backend.entities.ApplicationJobInfo;
import com.backend.entities.ApplicationJobSeekerInfo;
import com.backend.entities.ApplicationJobSeekerResume;
import com.backend.entities.Job;
import com.backend.entities.Role;
import com.backend.entities.User;
import com.backend.entities.UserResume;
import com.backend.repository.ApplicationRepository;
import com.backend.repository.JobRepository;
import com.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final CloudinaryService cloudinaryService;

    public ApplicationServiceImpl(
            ApplicationRepository applicationRepository,
            UserRepository userRepository,
            JobRepository jobRepository,
            CloudinaryService cloudinaryService
    ) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public ApplicationResponse applyForJob(
            Long jobId,
            String name,
            String email,
            String phone,
            String address,
            String coverLetter,
            MultipartFile resume,
            Authentication authentication
    ) {

        validateApplicationFields(
                name,
                email,
                phone,
                address,
                coverLetter
        );

        User currentUser = getCurrentUser(authentication);

        if (currentUser.getRole() != Role.JOB_SEEKER) {
            throw new RuntimeException(
                    "Only job seekers can apply for jobs"
            );
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new RuntimeException("Job not found")
                );

        boolean alreadyApplied =
                applicationRepository.existsByUserIdAndJobId(
                        currentUser.getId(),
                        jobId
                );

        if (alreadyApplied) {
            throw new RuntimeException(
                    "You have already applied for this job"
            );
        }

        String resumePublicId;
        String resumeUrl;

        if (resume != null && !resume.isEmpty()) {

            Map<String, Object> uploadResult =
                    cloudinaryService.uploadResume(resume);

            Object publicIdValue =
                    uploadResult.get("public_id");

            Object secureUrlValue =
                    uploadResult.get("secure_url");

            if (publicIdValue == null || secureUrlValue == null) {
                throw new RuntimeException(
                        "Failed to upload resume"
                );
            }

            resumePublicId =
                    String.valueOf(publicIdValue);

            resumeUrl =
                    String.valueOf(secureUrlValue);

        } else {

            UserResume existingResume =
                    getExistingResume(currentUser);

            resumePublicId =
                    existingResume.getPublicId();

            resumeUrl =
                    existingResume.getResumeUrl();
        }

        ApplicationJobSeekerInfo jobSeekerInfo =
                new ApplicationJobSeekerInfo();

        jobSeekerInfo.setUser(currentUser);
        jobSeekerInfo.setName(name.trim());
        jobSeekerInfo.setEmail(email.trim());
        jobSeekerInfo.setPhone(phone.trim());
        jobSeekerInfo.setAddress(address.trim());
        jobSeekerInfo.setCoverLetter(coverLetter.trim());
        jobSeekerInfo.setRole(Role.JOB_SEEKER);

        ApplicationJobSeekerResume applicationResume =
                new ApplicationJobSeekerResume();

        applicationResume.setPublicId(resumePublicId);
        applicationResume.setResumeUrl(resumeUrl);

        jobSeekerInfo.addResume(applicationResume);

        User employer = job.getPostedBy();

        if (employer == null) {
            throw new RuntimeException(
                    "Employer information is not available for this job"
            );
        }

        ApplicationEmployerInfo employerInfo =
                new ApplicationEmployerInfo();

        employerInfo.setEmployer(employer);
        employerInfo.setRole(Role.EMPLOYER);

        ApplicationJobInfo jobInfo =
                new ApplicationJobInfo();

        jobInfo.setJob(job);
        jobInfo.setJobTitle(job.getTitle());

        ApplicationDeletedBy deletedBy =
                new ApplicationDeletedBy();

        deletedBy.setJobSeeker(false);
        deletedBy.setEmployer(false);

        Application application = new Application();

        application.setJobSeekerInfo(jobSeekerInfo);
        application.setEmployerInfo(employerInfo);
        application.setJobInfo(jobInfo);
        application.setDeletedBy(deletedBy);

        Application savedApplication =
                applicationRepository.save(application);

        return convertToResponse(savedApplication);
    }

    @Override
    public List<ApplicationResponse> getMyApplications(
            Authentication authentication
    ) {

        User currentUser = getCurrentUser(authentication);

        if (currentUser.getRole() != Role.JOB_SEEKER) {
            throw new RuntimeException(
                    "Only job seekers can view their applications"
            );
        }

        List<Application> applications =
                applicationRepository.findAllByJobSeeker(
                        currentUser.getId()
                );

        List<ApplicationResponse> responses =
                new ArrayList<>();

        for (Application application : applications) {
            responses.add(convertToResponse(application));
        }

        return responses;
    }

    @Override
    public List<ApplicationResponse> getEmployerApplications(
            Authentication authentication
    ) {

        User currentUser = getCurrentUser(authentication);

        if (currentUser.getRole() != Role.EMPLOYER) {
            throw new RuntimeException(
                    "Only employers can view received applications"
            );
        }

        List<Application> applications =
                applicationRepository.findAllByEmployer(
                        currentUser.getId()
                );

        List<ApplicationResponse> responses =
                new ArrayList<>();

        for (Application application : applications) {
            responses.add(convertToResponse(application));
        }

        return responses;
    }

    @Override
    public void deleteApplication(
            Long applicationId,
            Authentication authentication
    ) {

        User currentUser = getCurrentUser(authentication);

        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found"
                                )
                        );

        if (currentUser.getRole() == Role.JOB_SEEKER) {

            Long applicationJobSeekerId =
                    application
                            .getJobSeekerInfo()
                            .getUser()
                            .getId();

            if (!applicationJobSeekerId.equals(
                    currentUser.getId()
            )) {
                throw new RuntimeException(
                        "You are not authorised to delete this application"
                );
            }

            // Job seeker deletes the complete application
            applicationRepository.delete(application);

        } else if (currentUser.getRole() == Role.EMPLOYER) {

            Long applicationEmployerId =
                    application
                            .getEmployerInfo()
                            .getEmployer()
                            .getId();

            if (!applicationEmployerId.equals(
                    currentUser.getId()
            )) {
                throw new RuntimeException(
                        "You are not authorised to delete this application"
                );
            }

            ApplicationDeletedBy deletedBy =
                    application.getDeletedBy();

            if (deletedBy == null) {

                deletedBy = new ApplicationDeletedBy();

                deletedBy.setJobSeeker(false);
                deletedBy.setEmployer(false);

                application.setDeletedBy(deletedBy);
            }

            // Only hide application from employer
            deletedBy.setEmployer(true);

            applicationRepository.save(application);

        } else {

            throw new RuntimeException(
                    "You are not authorised to delete this application"
            );
        }
    }

    private void validateApplicationFields(
            String name,
            String email,
            String phone,
            String address,
            String coverLetter
    ) {

        if (name == null || name.isBlank()) {
            throw new RuntimeException(
                    "Name is required"
            );
        }

        if (name.trim().length() < 3) {
            throw new RuntimeException(
                    "Name must contain at least 3 characters"
            );
        }

        if (email == null || email.isBlank()) {
            throw new RuntimeException(
                    "Email is required"
            );
        }

        if (!email.trim().matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        )) {
            throw new RuntimeException(
                    "Please provide a valid email"
            );
        }

        if (phone == null
                || !phone.trim().matches("^[0-9]{10}$")) {

            throw new RuntimeException(
                    "Phone number must contain exactly 10 digits"
            );
        }

        if (address == null || address.isBlank()) {
            throw new RuntimeException(
                    "Address is required"
            );
        }

        if (coverLetter == null || coverLetter.isBlank()) {
            throw new RuntimeException(
                    "Cover letter is required"
            );
        }

        if (coverLetter.trim().length() < 20) {
            throw new RuntimeException(
                    "Cover letter must contain at least 20 characters"
            );
        }
    }

    private User getCurrentUser(
            Authentication authentication
    ) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );
    }

    private UserResume getExistingResume(User user) {

        if (user.getResumes() == null
                || user.getResumes().isEmpty()) {

            throw new RuntimeException(
                    "Please upload your resume"
            );
        }

        UserResume existingResume =
                user.getResumes().get(0);

        if (existingResume.getResumeUrl() == null
                || existingResume.getResumeUrl().isBlank()) {

            throw new RuntimeException(
                    "Please upload your resume"
            );
        }

        return existingResume;
    }

    private ApplicationResponse convertToResponse(
            Application application
    ) {

        ApplicationResponse response =
                new ApplicationResponse();

        response.setApplicationId(
                application.getId()
        );

        ApplicationJobSeekerInfo jobSeekerInfo =
                application.getJobSeekerInfo();

        response.setJobSeekerId(
                jobSeekerInfo
                        .getUser()
                        .getId()
        );

        response.setJobSeekerName(
                jobSeekerInfo.getName()
        );

        response.setJobSeekerEmail(
                jobSeekerInfo.getEmail()
        );

        response.setJobSeekerPhone(
                jobSeekerInfo.getPhone()
        );

        response.setJobSeekerAddress(
                jobSeekerInfo.getAddress()
        );

        response.setCoverLetter(
                jobSeekerInfo.getCoverLetter()
        );

        ApplicationJobSeekerResume applicationResume =
                jobSeekerInfo.getResume();

        if (applicationResume != null) {

            response.setResumePublicId(
                    applicationResume.getPublicId()
            );

            response.setResumeUrl(
                    applicationResume.getResumeUrl()
            );
        }

        response.setEmployerId(
                application
                        .getEmployerInfo()
                        .getEmployer()
                        .getId()
        );

        response.setJobId(
                application
                        .getJobInfo()
                        .getJob()
                        .getId()
        );

        response.setJobTitle(
                application
                        .getJobInfo()
                        .getJobTitle()
        );

        response.setDeletedByJobSeeker(
                application
                        .getDeletedBy()
                        .getJobSeeker()
        );

        response.setDeletedByEmployer(
                application
                        .getDeletedBy()
                        .getEmployer()
        );

        response.setVersion(
                application.getVersion()
        );

        return response;
    }
}