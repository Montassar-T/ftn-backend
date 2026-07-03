package com.ftn.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
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

    @Column(name = "temps_ms")
    private Integer tempsMs;

    @Column(name = "temps_display", length = 20)
    private String tempsDisplay;

    @Column(name = "points_fina", precision = 8, scale = 2)
    private BigDecimal pointsFina;

    @Column(name = "tour", length = 50)
    private String tour;

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
