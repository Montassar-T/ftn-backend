package com.ftn.backend.dtos.licence;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.TypeLicenceEnum;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLicenceDto {

    @NotNull(message = "L'athlète est obligatoire")
    @JsonProperty("athlete_id")
    private Long athleteId;

    @NotNull(message = "Le club est obligatoire")
    @JsonProperty("club_id")
    private Long clubId;

    private String numero;

    @NotNull(message = "Le type de licence est obligatoire")
    private TypeLicenceEnum type;

    @JsonProperty("date_debut")
    private LocalDate dateDebut;

    @JsonProperty("date_expiration")
    private LocalDate dateExpiration;
}
