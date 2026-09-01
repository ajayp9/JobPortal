package com.backend.dto.response;

public class ApplicationResponse {

    private Long applicationId;

    private Long jobSeekerId;
    private String jobSeekerName;
    private String jobSeekerEmail;
    private String jobSeekerPhone;
    private String jobSeekerAddress;
    private String coverLetter;

    private String resumePublicId;
    private String resumeUrl;

    private Long employerId;

    private Long jobId;
    private String jobTitle;

    private Boolean deletedByJobSeeker;
    private Boolean deletedByEmployer;

    private Integer version;

    public ApplicationResponse() {
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getJobSeekerId() {
        return jobSeekerId;
    }

    public void setJobSeekerId(Long jobSeekerId) {
        this.jobSeekerId = jobSeekerId;
    }

    public String getJobSeekerName() {
        return jobSeekerName;
    }

    public void setJobSeekerName(String jobSeekerName) {
        this.jobSeekerName = jobSeekerName;
    }

    public String getJobSeekerEmail() {
        return jobSeekerEmail;
    }

    public void setJobSeekerEmail(String jobSeekerEmail) {
        this.jobSeekerEmail = jobSeekerEmail;
    }

    public String getJobSeekerPhone() {
        return jobSeekerPhone;
    }

    public void setJobSeekerPhone(String jobSeekerPhone) {
        this.jobSeekerPhone = jobSeekerPhone;
    }

    public String getJobSeekerAddress() {
        return jobSeekerAddress;
    }

    public void setJobSeekerAddress(String jobSeekerAddress) {
        this.jobSeekerAddress = jobSeekerAddress;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public String getResumePublicId() {
        return resumePublicId;
    }

    public void setResumePublicId(String resumePublicId) {
        this.resumePublicId = resumePublicId;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public Long getEmployerId() {
        return employerId;
    }

    public void setEmployerId(Long employerId) {
        this.employerId = employerId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Boolean getDeletedByJobSeeker() {
        return deletedByJobSeeker;
    }

    public void setDeletedByJobSeeker(Boolean deletedByJobSeeker) {
        this.deletedByJobSeeker = deletedByJobSeeker;
    }

    public Boolean getDeletedByEmployer() {
        return deletedByEmployer;
    }

    public void setDeletedByEmployer(Boolean deletedByEmployer) {
        this.deletedByEmployer = deletedByEmployer;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}