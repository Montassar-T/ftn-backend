package com.ftn.backend.dtos.resultat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultatDto {
    private Long id;
    private Long athleteId;
    private String athleteName;
    private String athleteNationality;
    private Long clubId;
    private String clubName;
    private Long eventId;
    private String eventLabel;
    private Long competitionId;
    private String competitionName;
    private Integer lane;
    private Integer tempsMs;
    private String tempsDisplay;
    private BigDecimal pointsFina;
    private String tour;
    private String status;
    private Integer rank;
    private Boolean isRecord;
    private Long validatedById;
    private LocalDateTime createdAt;
}
