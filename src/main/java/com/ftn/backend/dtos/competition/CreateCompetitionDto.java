package com.ftn.backend.dtos.competition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.CompetitionTypeEnum;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompetitionDto {

    @NotBlank(message = "Le nom de la compétition est obligatoire")
    private String nom;

    private CompetitionTypeEnum type;

    @JsonProperty("date_debut")
    private LocalDate dateDebut;

    @JsonProperty("date_fin")
    private LocalDate dateFin;

    @JsonProperty("pool_id")
    private Long poolId;

    private String description;
}
