package com.backend.repository;

import com.backend.entities.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Check if a job seeker has already applied for a job
    @Query("""
            SELECT COUNT(a) > 0
            FROM Application a
            WHERE a.jobSeekerInfo.user.id = :userId
            AND a.jobInfo.job.id = :jobId
            """)
    boolean existsByUserIdAndJobId(@Param("userId") Long userId,
                                   @Param("jobId") Long jobId);

    // Get all applications of a Job Seeker
    @Query("""
            SELECT a
            FROM Application a
            WHERE a.jobSeekerInfo.user.id = :userId
            AND a.deletedBy.jobSeeker = false
            """)
    List<Application> findAllByJobSeeker(@Param("userId") Long userId);

    // Get all applications received by an Employer
    @Query("""
            SELECT a
            FROM Application a
            WHERE a.employerInfo.employer.id = :employerId
            AND a.deletedBy.employer = false
            """)
    List<Application> findAllByEmployer(@Param("employerId") Long employerId);

    // Find application by id
    Optional<Application> findById(Long id);
}