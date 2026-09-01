package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "job_seeker_info_id",
            nullable = false,
            unique = true
    )
    private ApplicationJobSeekerInfo jobSeekerInfo;

    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "employer_info_id",
            nullable = false,
            unique = true
    )
    private ApplicationEmployerInfo employerInfo;

    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "job_info_id",
            nullable = false,
            unique = true
    )
    private ApplicationJobInfo jobInfo;

    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "deleted_by_id",
            nullable = false,
            unique = true
    )
    private ApplicationDeletedBy deletedBy;

    @Version
    @Column(nullable = false)
    private Integer version = 0;

    public void addJobSeekerInfo(
            ApplicationJobSeekerInfo jobSeekerInfo
    ) {
        this.jobSeekerInfo = jobSeekerInfo;
    }

    public void addEmployerInfo(
            ApplicationEmployerInfo employerInfo
    ) {
        this.employerInfo = employerInfo;
    }

    public void addJobInfo(
            ApplicationJobInfo jobInfo
    ) {
        this.jobInfo = jobInfo;
    }

    public void addDeletedBy(
            ApplicationDeletedBy deletedBy
    ) {
        this.deletedBy = deletedBy;
    }
}