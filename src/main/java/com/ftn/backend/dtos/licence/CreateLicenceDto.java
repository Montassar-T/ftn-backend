package com.ftn.backend.dtos.licence;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.TypeLicenceEnum;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLicenceDto {

    @JsonProperty("athlete_id")
    private Long athleteId;

    @JsonProperty("club_id")
    private Long clubId;

    private String numero;
    private TypeLicenceEnum type;

    @JsonProperty("date_debut")
    private LocalDate dateDebut;

    @JsonProperty("date_expiration")
    private LocalDate dateExpiration;
}
