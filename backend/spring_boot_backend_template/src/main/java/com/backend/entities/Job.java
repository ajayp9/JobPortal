package com.backend.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {
        "postedBy",
        "personalWebsites"
})
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            length = 255
    )
    private String title;

    @Column(
            name = "job_type",
            nullable = false,
            length = 100
    )
    private String jobType;

    @Column(
            nullable = false,
            length = 255
    )
    private String location;

    @Column(
            name = "company_name",
            nullable = false,
            length = 255
    )
    private String companyName;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @Column(columnDefinition = "TEXT")
    private String responsibilities;

    @Column(columnDefinition = "TEXT")
    private String qualifications;

    @Column(columnDefinition = "TEXT")
    private String offers;

    @Column(
            precision = 12,
            scale = 2
    )
    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "hiring_multiple_candidates",
            nullable = false,
            length = 50
    )
    private HiringMultipleCandidates hiringMultipleCandidates;

    @Column(
            name = "job_niche",
            length = 255
    )
    private String jobNiche;

    @Builder.Default
    @Column(
            name = "newsletters_sent",
            nullable = false
    )
    private Boolean newslettersSent = false;

    /*
     * Many jobs can be posted by one employer.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "posted_by",
            nullable = false
    )
    private User postedBy;

    @Column(
            name = "job_posted_on",
            nullable = false
    )
    private LocalDateTime jobPostedOn;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Integer version;

    /*
     * One job can contain zero or multiple
     * optional personal website links.
     */
    @Builder.Default
    @OneToMany(
            mappedBy = "job",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<JobPersonalWebsite> personalWebsites =
            new ArrayList<>();

    @PrePersist
    public void onCreate() {

        LocalDateTime currentTime =
                LocalDateTime.now();

        createdAt = currentTime;
        updatedAt = currentTime;

        if (jobPostedOn == null) {
            jobPostedOn = currentTime;
        }

        if (newslettersSent == null) {
            newslettersSent = false;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /*
     * Helper method for adding a website.
     */
    public void addPersonalWebsite(
            JobPersonalWebsite website) {

        if (website == null) {
            return;
        }

        personalWebsites.add(website);
        website.setJob(this);
    }

    /*
     * Helper method for removing a website.
     */
    public void removePersonalWebsite(
            JobPersonalWebsite website) {

        if (website == null) {
            return;
        }

        personalWebsites.remove(website);
        website.setJob(null);
    }
}