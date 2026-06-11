package com.ftn.backend.model;

import com.ftn.backend.enums.CompetitionStatutEnum;
import com.ftn.backend.enums.CompetitionTypeEnum;
import jakarta.persistence.*;
import java.time.LocalDate;
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

    @Column(nullable = false, length = 255)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private CompetitionTypeEnum type;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pool_id")
    private Pool pool;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    @Builder.Default
    private CompetitionStatutEnum statut = CompetitionStatutEnum.A_VENIR;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "nb_participants")
    @Builder.Default
    private Integer nbParticipants = 0;
}
