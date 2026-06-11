package com.ftn.backend.dtos.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.ResultStatutEnum;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultDto {
    private Long id;

    @JsonProperty("athlete_id")
    private Long athleteId;

    @JsonProperty("athlete_nom")
    private String athleteNom;

    @JsonProperty("competition_id")
    private Long competitionId;

    @JsonProperty("competition_nom")
    private String competitionNom;

    private String epreuve;
    private String temps;
    private Integer rang;
    private ResultStatutEnum statut;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
