package com.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "application_deleted_by")
@Getter
@Setter
@NoArgsConstructor
public class ApplicationDeletedBy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_seeker", nullable = false)
    private Boolean jobSeeker = false;

    @Column(name = "employer", nullable = false)
    private Boolean employer = false;
}