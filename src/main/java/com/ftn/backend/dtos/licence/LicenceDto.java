package com.ftn.backend.dtos.licence;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.StatutLicenceEnum;
import com.ftn.backend.enums.TypeLicenceEnum;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LicenceDto {
    private Long id;

    @JsonProperty("athlete_id")
    private Long athleteId;

    @JsonProperty("athlete_name")
    private String athleteName;

    @JsonProperty("club_id")
    private Long clubId;

    @JsonProperty("club_name")
    private String clubName;

    private String numero;
    private TypeLicenceEnum type;

    @JsonProperty("date_debut")
    private LocalDate dateDebut;

    @JsonProperty("date_expiration")
    private LocalDate dateExpiration;

    private StatutLicenceEnum statut;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
