package com.ftn.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "nations_ranking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NationsRanking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "athlete_id", nullable = false)
    private Athlete athlete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "best_time_ms")
    private Long bestTimeMs;

    @Column(name = "points_fina", precision = 8, scale = 2)
    private BigDecimal pointsFina;

    @Column(name = "rank_position")
    private Integer rankPosition;

    @Column(length = 10)
    private String season;
}
