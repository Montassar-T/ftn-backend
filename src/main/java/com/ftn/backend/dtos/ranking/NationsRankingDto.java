package com.ftn.backend.dtos.ranking;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NationsRankingDto {
    private Long id;
    private Long athleteId;
    private String athleteNom;
    private String athletePrenom;
    private Long eventId;
    private Long bestTimeMs;
    private String bestTimeDisplay;
    private BigDecimal pointsFina;
    private Integer rankPosition;
    private String season;
    private LocalDateTime createdAt;
}
