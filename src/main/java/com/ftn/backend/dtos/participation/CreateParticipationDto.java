package com.ftn.backend.dtos.participation;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateParticipationDto {

    @NotNull
    private Long evenementId;

    @NotNull
    private Long userId;

    private String message;
}
