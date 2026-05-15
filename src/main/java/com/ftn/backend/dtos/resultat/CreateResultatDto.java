package com.ftn.backend.dtos.resultat;

import jakarta.validation.constraints.NotNull;
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

    private Integer finalTime;

    @NotNull
    private Integer rank;

    private Boolean isRecord;

    private Long validatedById;
}
