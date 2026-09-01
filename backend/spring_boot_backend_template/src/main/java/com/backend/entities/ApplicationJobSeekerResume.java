package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "application_job_seeker_resume")
@Getter
@Setter
@NoArgsConstructor
public class ApplicationJobSeekerResume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "job_seeker_info_id",
            nullable = false,
            unique = true
    )
    private ApplicationJobSeekerInfo jobSeekerInfo;

    @Column(name = "public_id", length = 255)
    private String publicId;

    @Column(name = "resume_url", columnDefinition = "TEXT")
    private String resumeUrl;
}