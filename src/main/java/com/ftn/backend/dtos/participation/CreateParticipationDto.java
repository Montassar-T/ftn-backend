package com.ftn.backend.dtos.participation;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateParticipationDto {

    // Rempli par EvenementController#register() depuis le path variable {id} après validation :
    // ne doit pas être @NotNull, sinon un appel conforme au contrat (sans evenementId dans le body) échoue.
    private Long evenementId;

    @NotNull
    private Long userId;

    private String message;
}
