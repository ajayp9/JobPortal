package com.backend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_resumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many resumes belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "public_id", length = 255)
    private String publicId;

    @Column(name = "resume_url", columnDefinition = "TEXT")
    private String resumeUrl;

}