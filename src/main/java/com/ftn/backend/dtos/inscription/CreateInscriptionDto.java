package com.ftn.backend.dtos.inscription;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateInscriptionDto {

    @NotNull(message = "Athlete is required")
    private Long athleteId;

    @NotNull(message = "Event is required")
    private Long eventId;

    private String seedTime;
}
