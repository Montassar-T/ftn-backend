package com.ftn.backend.dtos.competition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.CompetitionStatutEnum;
import com.ftn.backend.enums.CompetitionTypeEnum;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionDto {
    private Long id;

    // --- HEAD fields ---
    private String nom;
    private CompetitionTypeEnum type;

    @JsonProperty("date_debut")
    private LocalDate dateDebut;

    @JsonProperty("date_fin")
    private LocalDate dateFin;

    @JsonProperty("pool_id")
    private Long poolId;

    @JsonProperty("pool_nom")
    private String poolNom;

    private CompetitionStatutEnum statut;
    private String description;

    @JsonProperty("nb_participants")
    private Integer nbParticipants;

    // --- Upstream fields ---
    private String code;
    private String name;
    private String typeStr;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime registrationDeadline;
    private String lane;
    private String ageCategories;
    private String sourceUrl;
    private Long createdById;
    private String status;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
