package com.ftn.backend.dtos.resultat;

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
    private Long eventId;
    private Integer lane;
    private Integer finalTime;
    private String status;
    private Integer rank;
    private Boolean isRecord;
    private Long validatedById;
    private LocalDateTime createdAt;
}
