package com.ftn.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(
        name = "classements",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_classement_scope",
                    columnNames = {"athlete_id", "epreuve_id", "season"})
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "epreuve_id", nullable = false)
    private Epreuve epreuve;

    @Column(name = "best_time_ms")
    private Integer bestTimeMs;

    @Column(name = "best_time_display", length = 20)
    private String bestTimeDisplay;

    @Column(name = "points_fina", precision = 8, scale = 2)
    private BigDecimal pointsFina;

    @Column(nullable = false)
    private Integer rank;

    @Column(nullable = false, length = 20)
    private String season;
}
