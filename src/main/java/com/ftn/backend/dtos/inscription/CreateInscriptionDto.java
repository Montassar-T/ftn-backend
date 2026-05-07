package com.ftn.backend.dtos.inscription;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateInscriptionDto {

    @JsonProperty("athlete_id")
    private Long athleteId;

    @JsonProperty("epreuve_id")
    private Long epreuveId;
}
