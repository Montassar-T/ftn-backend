package com.ftn.backend.dtos.resultat;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateResultatDto {
    private String temps;
    private Integer classement;
    private Boolean record;
    private Integer points;
}
