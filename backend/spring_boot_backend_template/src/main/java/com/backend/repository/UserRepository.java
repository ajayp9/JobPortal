package com.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.entities.User;

public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    @Query("""
            SELECT DISTINCT u
            FROM User u
            JOIN u.userNiches un
            JOIN un.niche n
            WHERE LOWER(n.name) = LOWER(:jobNiche)
            """)
    List<User> findUsersByJobNiche(
            @Param("jobNiche") String jobNiche
    );
}