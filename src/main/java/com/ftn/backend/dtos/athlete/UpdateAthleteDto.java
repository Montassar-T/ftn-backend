package com.ftn.backend.dtos.athlete;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.CategorieEnum;
import com.ftn.backend.enums.SexeEnum;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAthleteDto {

    private String nom;
    private String prenom;

    @JsonProperty("club_id")
    private Long clubId;

    @JsonProperty("date_naissance")
    private LocalDate dateNaissance;

    private String nationalite;
    private CategorieEnum categorie;
    private SexeEnum sexe;
    private String email;
    private String telephone;
}
