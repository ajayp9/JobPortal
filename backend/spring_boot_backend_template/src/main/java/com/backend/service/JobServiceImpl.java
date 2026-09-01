package com.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.dto.request.JobRequest;
import com.backend.dto.response.JobResponse;
import com.backend.dto.response.PersonalWebsiteResponse;
import com.backend.entities.Job;
import com.backend.entities.JobPersonalWebsite;
import com.backend.entities.Role;
import com.backend.entities.User;
import com.backend.exception.BadRequestException;
import com.backend.exception.ResourceNotFoundException;
import com.backend.repository.JobRepository;
import com.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    /*
     * Post a new job.
     * Only an EMPLOYER can post a job.
     */
    @Override
    public JobResponse postJob(
            String employerEmail,
            JobRequest request) {

        User employer =
                findEmployerByEmail(employerEmail);

        validateWebsiteDetails(request);

        Job job = new Job();

        job.setTitle(
                request.getTitle().trim()
        );

        job.setJobType(
                request.getJobType().trim()
        );

        job.setLocation(
                request.getLocation().trim()
        );

        job.setCompanyName(
                request.getCompanyName().trim()
        );

        job.setIntroduction(
                request.getIntroduction().trim()
        );

        job.setResponsibilities(
                request.getResponsibilities().trim()
        );

        job.setQualifications(
                request.getQualifications().trim()
        );

        /*
         * Offers is optional.
         */
        if (request.getOffers() != null
                && !request.getOffers().isBlank()) {

            job.setOffers(
                    request.getOffers().trim()
            );

        } else {

            job.setOffers(null);
        }

        job.setSalary(
                request.getSalary()
        );

        /*
         * Current Job entity has nullable = false,
         * so this field must be supplied.
         */
        if (request.getHiringMultipleCandidates()
                == null) {

            throw new BadRequestException(
                    "Hiring multiple candidates value is required"
            );
        }

        job.setHiringMultipleCandidates(
                request.getHiringMultipleCandidates()
        );

        job.setJobNiche(
                request.getJobNiche().trim()
        );

        job.setPostedBy(employer);
        job.setNewslettersSent(false);

        /*
         * Website is optional.
         * Create the website entity only when both
         * title and URL are supplied.
         */
        if (hasText(
                request.getPersonalWebsiteTitle())
                && hasText(
                request.getPersonalWebsiteUrl())) {

            JobPersonalWebsite website =
                    new JobPersonalWebsite();

            website.setWebsiteTitle(
                    request
                            .getPersonalWebsiteTitle()
                            .trim()
            );

            website.setWebsiteUrl(
                    request
                            .getPersonalWebsiteUrl()
                            .trim()
            );

            job.addPersonalWebsite(website);
        }

        Job savedJob =
                jobRepository.save(job);

        jobRepository.flush();

        return convertToResponse(savedJob);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getAllJobs(
            String city,
            String niche,
            String searchKeyword) {

        String normalizedCity =
                normalizeOptionalValue(city);

        String normalizedNiche =
                normalizeOptionalValue(niche);

        String normalizedKeyword =
                normalizeOptionalValue(searchKeyword);

        List<Job> jobs;

        int filterCount = 0;

        if (normalizedCity != null) {
            filterCount++;
        }

        if (normalizedNiche != null) {
            filterCount++;
        }

        if (normalizedKeyword != null) {
            filterCount++;
        }

        /*
         * No filter
         */
        if (filterCount == 0) {

            jobs = jobRepository.findAll();

        /*
         * Only one filter: use derived query
         */
        } else if (filterCount == 1) {

            if (normalizedCity != null) {

                jobs = jobRepository
                        .findByLocationContainingIgnoreCase(
                                normalizedCity
                        );

            } else if (normalizedNiche != null) {

                jobs = jobRepository
                        .findByJobNicheContainingIgnoreCase(
                                normalizedNiche
                        );

            } else {

//                jobs = jobRepository
//                        .findByTitleContainingIgnoreCaseOrCompanyNameContainingIgnoreCaseOrIntroductionContainingIgnoreCaseOrJobTypeContainingIgnoreCase(
//                                normalizedKeyword,
//                                normalizedKeyword,
//                                normalizedKeyword,
//                                normalizedKeyword
//                        );
            	jobs = jobRepository
            	        .findByTitleContainingIgnoreCaseOrCompanyNameContainingIgnoreCaseOrIntroductionContainingIgnoreCaseOrJobTypeContainingIgnoreCaseOrLocationContainingIgnoreCase(
            	                normalizedKeyword,
            	                normalizedKeyword,
            	                normalizedKeyword,
            	                normalizedKeyword,
            	                normalizedKeyword
            	        );
            }

        /*
         * Two or three filters: use JPQL
         */
        } else {

            jobs = jobRepository
                    .searchJobsWithMultipleFilters(
                            normalizedCity,
                            normalizedNiche,
                            normalizedKeyword
                    );
        }

        return convertToResponseList(jobs);
    }

    /*
     * Get one job using its ID.
     * This endpoint can be public.
     */
    @Override
    @Transactional(readOnly = true)
    public JobResponse getJobById(
            Long jobId) {

        Job job =
                findJobById(jobId);

        return convertToResponse(job);
    }

    /*
     * Get jobs posted by the currently
     * authenticated employer.
     */
    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> getMyJobs(
            String employerEmail) {

        User employer =
                findEmployerByEmail(employerEmail);

        List<Job> jobs =
                jobRepository.findByPostedBy(
                        employer
                );

        return convertToResponseList(jobs);
    }

    /*
     * Delete a job.
     *
     * An employer can delete only a job
     * posted by that employer.
     */
    @Override
    public void deleteJob(
            String employerEmail,
            Long jobId) {

        User employer =
                findEmployerByEmail(employerEmail);

        Job job =
                findJobById(jobId);

        if (job.getPostedBy() == null
                || !job.getPostedBy()
                .getId()
                .equals(employer.getId())) {

            throw new BadRequestException(
                    "You can delete only your own jobs"
            );
        }

        jobRepository.delete(job);
    }

    /*
     * Validate the optional website fields.
     *
     * Both fields must be supplied together,
     * or both must be blank.
     */
    private void validateWebsiteDetails(
            JobRequest request) {

        boolean titleProvided =
                hasText(
                        request.getPersonalWebsiteTitle()
                );

        boolean urlProvided =
                hasText(
                        request.getPersonalWebsiteUrl()
                );

        if (titleProvided != urlProvided) {

            throw new BadRequestException(
                    "Provide both the website URL and title, or leave both blank"
            );
        }
    }

    /*
     * Find the authenticated employer.
     */
    private User findEmployerByEmail(
            String employerEmail) {

        if (!hasText(employerEmail)) {

            throw new BadRequestException(
                    "Employer email is required"
            );
        }

        String normalizedEmail =
                employerEmail
                        .trim()
                        .toLowerCase();

        User employer = userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employer not found with email: "
                                        + normalizedEmail
                        )
                );

        if (employer.getRole()
                != Role.EMPLOYER) {

            throw new BadRequestException(
                    "Only employers can perform this operation"
            );
        }

        if (Boolean.FALSE.equals(
                employer.getActive())) {

            throw new BadRequestException(
                    "Employer account is inactive"
            );
        }

        return employer;
    }

    /*
     * Find a job using its ID.
     */
    private Job findJobById(
            Long jobId) {

        if (jobId == null) {

            throw new BadRequestException(
                    "Job ID is required"
            );
        }

        return jobRepository
                .findById(jobId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job not found with id: "
                                        + jobId
                        )
                );
    }

    /*
     * Check title, company name and introduction
     * for the supplied keyword.
     */
    private boolean matchesKeyword(
            Job job,
            String searchKeyword) {

        String keyword =
                searchKeyword.toLowerCase(
                        Locale.ROOT
                );

        return containsIgnoreCase(
                job.getTitle(),
                keyword
        )
                || containsIgnoreCase(
                job.getCompanyName(),
                keyword
        )
                || containsIgnoreCase(
                job.getIntroduction(),
                keyword
        );
    }

    /*
     * Case-insensitive contains check.
     */
    private boolean containsIgnoreCase(
            String value,
            String lowerCaseKeyword) {

        return value != null
                && value.toLowerCase(
                Locale.ROOT
        ).contains(lowerCaseKeyword);
    }

    /*
     * Convert blank optional values to null.
     */
    private String normalizeOptionalValue(
            String value) {

        if (!hasText(value)) {
            return null;
        }

        return value.trim();
    }

    /*
     * Check whether a string contains text.
     */
    private boolean hasText(
            String value) {

        return value != null
                && !value.isBlank();
    }

    /*
     * Convert a list of Job entities
     * into JobResponse DTOs.
     */
    private List<JobResponse> convertToResponseList(
            List<Job> jobs) {

        List<JobResponse> responses =
                new ArrayList<>();

        for (Job job : jobs) {

            responses.add(
                    convertToResponse(job)
            );
        }

        return responses;
    }

    /*
     * Convert Job entity into JobResponse DTO.
     */
    private JobResponse convertToResponse(
            Job job) {

        JobResponse response =
                new JobResponse();

        response.setId(
                job.getId()
        );

        response.setTitle(
                job.getTitle()
        );

        response.setJobType(
                job.getJobType()
        );

        response.setLocation(
                job.getLocation()
        );

        response.setCompanyName(
                job.getCompanyName()
        );

        response.setIntroduction(
                job.getIntroduction()
        );

        response.setResponsibilities(
                job.getResponsibilities()
        );

        response.setQualifications(
                job.getQualifications()
        );

        response.setOffers(
                job.getOffers()
        );

        response.setSalary(
                job.getSalary()
        );

        response.setHiringMultipleCandidates(
                job.getHiringMultipleCandidates()
        );

        response.setJobNiche(
                job.getJobNiche()
        );

        response.setNewslettersSent(
                job.getNewslettersSent()
        );

        response.setJobPostedOn(
                job.getJobPostedOn()
        );

        response.setCreatedAt(
                job.getCreatedAt()
        );

        response.setUpdatedAt(
                job.getUpdatedAt()
        );

        /*
         * Add employer information.
         */
        if (job.getPostedBy() != null) {

            response.setEmployerId(
                    job.getPostedBy().getId()
            );

            response.setEmployerName(
                    job.getPostedBy().getName()
            );

            response.setEmployerEmail(
                    job.getPostedBy().getEmail()
            );
        }

        /*
         * Convert personal websites.
         */
        List<PersonalWebsiteResponse>
                websiteResponses =
                new ArrayList<>();

        if (job.getPersonalWebsites()
                != null) {

            for (JobPersonalWebsite website
                    : job.getPersonalWebsites()) {

                PersonalWebsiteResponse
                        websiteResponse =
                        new PersonalWebsiteResponse();

                websiteResponse.setId(
                        website.getId()
                );

                websiteResponse.setWebsiteTitle(
                        website.getWebsiteTitle()
                );

                websiteResponse.setWebsiteUrl(
                        website.getWebsiteUrl()
                );

                websiteResponses.add(
                        websiteResponse
                );
            }
        }

        response.setPersonalWebsites(
                websiteResponses
        );

        return response;
    }
}