package com.ftn.backend.dtos.classement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RebuildClassementDto {

    @NotNull
    private Long eventId;

    @NotBlank
    private String season;
}
