package com.ftn.backend.dtos.resultat;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateResultatDto {
    private Integer lane;
    private Integer finalTime;
    private String status;
    private Integer rank;
    private Boolean isRecord;
    private Long validatedById;
}
