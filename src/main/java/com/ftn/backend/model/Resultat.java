package com.ftn.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(
        name = "resultats",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_resultat_epreuve_athlete",
                    columnNames = {"epreuve_id", "athlete_id"})
        })
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
    @JoinColumn(name = "epreuve_id", nullable = false)
    private Epreuve epreuve;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "athlete_id", nullable = false)
    private Athlete athlete;

    @Column(nullable = false, length = 20)
    private String temps;

    @Column(nullable = false)
    private Integer classement;

    @Column(nullable = false)
    @Builder.Default
    private Boolean record = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer points = 0;

    @Column(name = "date_saisie", nullable = false)
    private LocalDateTime dateSaisie;

    @Column(nullable = false)
    @Builder.Default
    private Boolean publie = false;
}
