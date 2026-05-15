package com.ftn.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resultats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resultat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "athlete_id", nullable = false)
    private Athlete athlete;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "epreuve_id", nullable = false)
    private Epreuve epreuve;

    @Column(name = "lane")
    private Integer lane;

    @Column(name = "final_time")
    private Integer finalTime;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String status = "EN_ATTENTE";

    @Column(nullable = false)
    private Integer rank;

    @Column(name = "is_record", nullable = false)
    @Builder.Default
    private Boolean isRecord = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by")
    private User validatedBy;
}
