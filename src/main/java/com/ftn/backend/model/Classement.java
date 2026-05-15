package com.ftn.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "classements",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_classement_scope",
                    columnNames = {"athlete_id", "swim_style", "distance", "season"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Classement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "athlete_id", nullable = false)
    private Athlete athlete;

    @Column(name = "swim_style", nullable = false, length = 50)
    private String swimStyle;

    @Column(nullable = false, length = 100)
    private String distance;

    @Column(name = "best_time")
    private Integer bestTime;

    @Column(nullable = false)
    private Integer rank;

    @Column(nullable = false, length = 20)
    private String season;
}
