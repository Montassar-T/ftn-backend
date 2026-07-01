package com.ftn.backend.dtos.inscription;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionDto {
    private Long id;
    private Long athleteId;
    private Long eventId;
    private String seedTime;
    private String status;
    private LocalDateTime registeredAt;
    private LocalDateTime createdAt;

    @JsonProperty("athlete_nom")
    private String athleteNom;

    @JsonProperty("athlete_prenom")
    private String athletePrenom;

    @JsonProperty("epreuve_label")
    private String epreuveLabel;

    @JsonProperty("queue_position")
    private Integer queuePosition;

    @JsonProperty("competition_id")
    private Long competitionId;
}
