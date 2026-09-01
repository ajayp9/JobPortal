package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.backend.entities.UserNiche;

public interface UserNicheRepository
        extends JpaRepository<UserNiche, Long> {

    @Transactional
    void deleteByUserId(Long userId);
}