package com.ftn.backend.dtos.resultat;

import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateResultatDto {
    private Integer lane;
    private Integer tempsMs;
    private String tempsDisplay;
    private BigDecimal pointsFina;
    private String tour;
    private String status;
    private Integer rank;
    private Boolean isRecord;
    private Long validatedById;
}
