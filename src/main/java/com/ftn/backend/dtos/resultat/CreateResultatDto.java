package com.ftn.backend.dtos.resultat;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateResultatDto {

    @NotNull
    private Long athleteId;

    @NotNull
    private Long eventId;

    private Integer lane;

    private Integer tempsMs;

    private String tempsDisplay;

    private BigDecimal pointsFina;

    private String tour;

    @NotNull
    private Integer rank;

    private Boolean isRecord;

    private Long validatedById;
}
