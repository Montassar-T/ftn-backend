package com.ftn.backend.dtos.resultat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateResultatDto {

    @NotNull
    private Long epreuveId;

    @NotNull
    private Long athleteId;

    @NotBlank
    private String temps;

    @NotNull
    private Integer classement;

    private Boolean record;

    private Integer points;
}
