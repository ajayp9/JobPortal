package com.backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "niches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Niche {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "niche")
    private List<UserNiche> userNiches = new ArrayList<>();

}