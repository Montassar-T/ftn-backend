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
    private Long epreuveId;
    private Long athleteId;
    private String temps;
    private Integer classement;
    private Boolean record;
    private Integer points;
    private LocalDateTime dateSaisie;
    private Boolean publie;
    private LocalDateTime createdAt;
}
