package com.ftn.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.dtos.competition.CompetitionDto;
import java.util.List;
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

    @JsonProperty("nb_licences")
    private Long nbLicences;

    @JsonProperty("nb_results")
    private Long nbResults;

    @JsonProperty("nb_actualites")
    private Long nbActualites;

    @JsonProperty("nb_staff")
    private Long nbStaff;

    @JsonProperty("nb_active_competitions")
    private Long nbActiveCompetitions;

    @JsonProperty("recent_competitions")
    private List<CompetitionDto> recentCompetitions;
}
