package com.ftn.backend.dtos.competition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.CompetitionStatutEnum;
import com.ftn.backend.enums.CompetitionTypeEnum;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompetitionDto {
    private String nom;
    private CompetitionTypeEnum type;

    @JsonProperty("date_debut")
    private LocalDate dateDebut;

    @JsonProperty("date_fin")
    private LocalDate dateFin;

    @JsonProperty("pool_id")
    private Long poolId;

    private CompetitionStatutEnum statut;
    private String description;

    @JsonProperty("nb_participants")
    private Integer nbParticipants;
}
