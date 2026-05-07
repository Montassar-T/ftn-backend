package com.ftn.backend.dtos.inscription;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateInscriptionDto {

    @NotNull(message = "L'athlète est obligatoire")
    @JsonProperty("athlete_id")
    private Long athleteId;

    @JsonProperty("epreuve_id")
    private Long epreuveId;
}
