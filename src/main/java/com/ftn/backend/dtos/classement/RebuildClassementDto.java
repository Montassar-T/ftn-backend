package com.ftn.backend.dtos.classement;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RebuildClassementDto {

    @NotBlank
    private String swimStyle;

    @NotBlank
    private String distance;

    @NotBlank
    private String season;
}
