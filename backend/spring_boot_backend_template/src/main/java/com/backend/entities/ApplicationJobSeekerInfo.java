package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "application_job_seeker_info")
@Getter
@Setter
@NoArgsConstructor
public class ApplicationJobSeekerInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One user can apply to many jobs.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @OneToOne(
            mappedBy = "jobSeekerInfo",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private ApplicationJobSeekerResume resume;

    public void addResume(ApplicationJobSeekerResume resume) {
        this.resume = resume;

        if (resume != null) {
            resume.setJobSeekerInfo(this);
        }
    }
}