package com.ftn.backend.dtos.inscription;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateInscriptionDto {

    @NotNull(message = "Athlete is required")
    @JsonProperty("athlete_id")
    private Long athleteId;

    @NotNull(message = "Epreuve is required")
    @JsonProperty("epreuve_id")
    private Long epreuveId;
}
