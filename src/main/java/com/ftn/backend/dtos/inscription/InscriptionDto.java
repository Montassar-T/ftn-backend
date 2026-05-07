package com.ftn.backend.dtos.inscription;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.StatutInscEnum;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionDto {
    private Long id;

    @JsonProperty("athlete_id")
    private Long athleteId;

    @JsonProperty("epreuve_id")
    private Long epreuveId;

    @JsonProperty("date_inscription")
    private LocalDateTime dateInscription;

    private StatutInscEnum statut;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
