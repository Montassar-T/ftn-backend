package com.ftn.backend.dtos.club;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClubDto {
    private String nom;
    private String ville;
    private String region;
    private String logo;

    @JsonProperty("date_affiliation")
    private LocalDate dateAffiliation;

    private Boolean actif;

    @JsonProperty("president_nom")
    private String presidentNom;
}
