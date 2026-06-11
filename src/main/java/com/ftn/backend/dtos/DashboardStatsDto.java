package com.ftn.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {

    @JsonProperty("nb_athletes")
    private Long nbAthletes;

    @JsonProperty("nb_competitions")
    private Long nbCompetitions;

    @JsonProperty("nb_clubs")
    private Long nbClubs;

    @JsonProperty("nb_piscines")
    private Long nbPiscines;
}
