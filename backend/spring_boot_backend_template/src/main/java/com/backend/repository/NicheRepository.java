package com.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entities.Niche;

public interface NicheRepository
        extends JpaRepository<Niche, Long> {

    Optional<Niche> findByNameIgnoreCase(String name);
}