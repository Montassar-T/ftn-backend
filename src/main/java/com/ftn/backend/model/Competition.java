package com.ftn.backend.model;

import com.ftn.backend.enums.CompetitionStatutEnum;
import com.ftn.backend.enums.CompetitionTypeEnum;
import jakarta.persistence.*;
import java.time.LocalDate;
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

    // --- HEAD fields (French naming, enum-based) ---

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

    // --- Upstream fields (competition management module) ---

    @Column(name = "name")
    private String name;

    @Column(name = "start_date", nullable = true)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = true)
    private LocalDate endDate;

    @Column(name = "registration_deadline")
    private LocalDateTime registrationDeadline;

    @Column(length = 50)
    private String code;

    @Column(length = 10)
    private String lane;

    @Column(name = "age_categories", length = 200)
    private String ageCategories;

    @Column(name = "source_url", columnDefinition = "TEXT")
    private String sourceUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String status = "PLANIFIEE";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;
}
