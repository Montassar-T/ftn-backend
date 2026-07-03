package com.ftn.backend.dtos.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.ResultStatutEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateResultDto {
    private String epreuve;
    private String temps;
    private Integer rang;
    private ResultStatutEnum statut;

    @JsonProperty("athlete_id")
    private Long athleteId;

    @JsonProperty("competition_id")
    private Long competitionId;
}
