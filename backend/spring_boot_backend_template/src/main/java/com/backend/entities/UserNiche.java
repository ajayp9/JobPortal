package com.backend.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_niches",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "niche_id"}
                )
        }
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "niche"})
public class UserNiche {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many UserNiche records belong to one User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Many UserNiche records belong to one Niche
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "niche_id", nullable = false)
    private Niche niche;

}