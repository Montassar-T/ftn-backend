package com.ftn.backend.dtos.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateResultDto {

    @NotNull(message = "L'athlète est obligatoire")
    @JsonProperty("athlete_id")
    private Long athleteId;

    @NotNull(message = "La compétition est obligatoire")
    @JsonProperty("competition_id")
    private Long competitionId;

    @NotBlank(message = "L'épreuve est obligatoire")
    private String epreuve;

    private String temps;
    private Integer rang;
}
