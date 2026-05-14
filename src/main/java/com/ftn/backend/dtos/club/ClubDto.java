package com.ftn.backend.dtos.club;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClubDto {
    private Long id;
    private String nom;
    private String ville;
    private String region;
    private String logo;

    @JsonProperty("date_affiliation")
    private LocalDate dateAffiliation;

    private Boolean actif;

    @JsonProperty("president_nom")
    private String presidentNom;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
