package com.ftn.backend.dtos.resultat;

import java.math.BigDecimal;
import java.util.List;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AthleteStatsDto {
    private Long athleteId;
    private String athleteName;
    private int totalResults;
    private int validated;
    private int records;
    private BigDecimal bestPointsFina;
    private Integer bestRank;
    private List<PersonalBestDto> personalBests;

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonalBestDto {
        private Long eventId;
        private String eventLabel;
        private String tempsDisplay;
        private Integer tempsMs;
        private BigDecimal pointsFina;
        private Integer rank;
        private Boolean isRecord;
        private String competitionName;
    }
}
