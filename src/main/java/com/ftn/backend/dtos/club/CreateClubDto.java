package com.ftn.backend.dtos.club;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateClubDto {

    @NotBlank(message = "Le nom du club est obligatoire")
    private String nom;

    private String ville;
    private String region;
    private String logo;

    @JsonProperty("date_affiliation")
    private LocalDate dateAffiliation;

    private Boolean actif;

    @JsonProperty("president_nom")
    private String presidentNom;

    @JsonProperty("pool_id")
    private Long poolId;

    private Integer lane;
}
