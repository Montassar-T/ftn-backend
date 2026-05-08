package com.ftn.backend.model;

import com.ftn.backend.enums.DisciplineEnum;
import com.ftn.backend.enums.NiveauEnum;
import com.ftn.backend.enums.StatutCompEnum;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "competitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Competition extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private DisciplineEnum discipline;

    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDateTime dateFin;

    @Column(nullable = false)
    private String lieu;

    private String region;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NiveauEnum niveau;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private StatutCompEnum statut = StatutCompEnum.PLANIFIEE;
}
