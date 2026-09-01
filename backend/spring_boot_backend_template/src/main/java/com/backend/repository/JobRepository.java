package com.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.entities.Job;
import com.backend.entities.User;

public interface JobRepository
        extends JpaRepository<Job, Long> {

    // Get jobs posted by logged-in employer
    List<Job> findByPostedBy(User postedBy);

    // Search only by city
    List<Job> findByLocationContainingIgnoreCase(
            String city
    );

    // Search only by niche
    List<Job> findByJobNicheContainingIgnoreCase(
            String niche
    );

    // Search only by keyword
//    List<Job>
//    findByTitleContainingIgnoreCaseOrCompanyNameContainingIgnoreCaseOrIntroductionContainingIgnoreCaseOrJobTypeContainingIgnoreCase(
//            String title,
//            String companyName,
//            String introduction,
//            String jobType
//    );
    List<Job> findByTitleContainingIgnoreCaseOrCompanyNameContainingIgnoreCaseOrIntroductionContainingIgnoreCaseOrJobTypeContainingIgnoreCaseOrLocationContainingIgnoreCase(
            String title,
            String companyName,
            String introduction,
            String jobType,
            String location
    );

    /*
     * Use JPQL when two or more filters are supplied.
     */
//    @Query("""
//            SELECT j
//            FROM Job j
//            WHERE
//                (:city IS NULL
//                    OR LOWER(j.location)
//                    LIKE LOWER(CONCAT('%', :city, '%')))
//
//            AND (:niche IS NULL
//                    OR LOWER(j.jobNiche)
//                    LIKE LOWER(CONCAT('%', :niche, '%')))
//
//            AND (
//                    :searchKeyword IS NULL
//
//                    OR LOWER(j.title)
//                    LIKE LOWER(CONCAT('%', :searchKeyword, '%'))
//
//                    OR LOWER(j.companyName)
//                    LIKE LOWER(CONCAT('%', :searchKeyword, '%'))
//
//                    OR LOWER(j.introduction)
//                    LIKE LOWER(CONCAT('%', :searchKeyword, '%'))
//
//                    OR LOWER(j.jobType)
//                    LIKE LOWER(CONCAT('%', :searchKeyword, '%'))
//                )
//            """)
    @Query("""
            SELECT j
            FROM Job j
            WHERE
                (:city IS NULL
                    OR LOWER(j.location)
                    LIKE LOWER(CONCAT('%', :city, '%')))

            AND (:niche IS NULL
                    OR LOWER(j.jobNiche)
                    LIKE LOWER(CONCAT('%', :niche, '%')))

            AND (
                    :searchKeyword IS NULL

                    OR LOWER(j.title)
                    LIKE LOWER(CONCAT('%', :searchKeyword, '%'))

                    OR LOWER(j.companyName)
                    LIKE LOWER(CONCAT('%', :searchKeyword, '%'))

                    OR LOWER(j.introduction)
                    LIKE LOWER(CONCAT('%', :searchKeyword, '%'))

                    OR LOWER(j.jobType)
                    LIKE LOWER(CONCAT('%', :searchKeyword, '%'))

                    OR LOWER(j.location)
                    LIKE LOWER(CONCAT('%', :searchKeyword, '%'))

                    OR LOWER(j.jobNiche)
                    LIKE LOWER(CONCAT('%', :searchKeyword, '%'))

                    OR LOWER(j.qualifications)
                    LIKE LOWER(CONCAT('%', :searchKeyword, '%'))

                    OR LOWER(j.responsibilities)
                    LIKE LOWER(CONCAT('%', :searchKeyword, '%'))
                )
            """)
    List<Job> searchJobsWithMultipleFilters(
            @Param("city") String city,
            @Param("niche") String niche,
            @Param("searchKeyword") String searchKeyword
    );

    // Get jobs where newsletter email is not sent
    List<Job> findByNewslettersSentFalse();
}