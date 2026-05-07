package com.ftn.backend.dtos.athlete;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.CategorieEnum;
import com.ftn.backend.enums.SexeEnum;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAthleteDto {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("club_id")
    private Long clubId;

    @NotNull(message = "La date de naissance est obligatoire")
    @JsonProperty("date_naissance")
    private LocalDate dateNaissance;

    @NotNull(message = "La nationalité est obligatoire")
    private String nationalite;

    @NotNull(message = "La catégorie est obligatoire")
    private CategorieEnum categorie;

    @NotNull(message = "Le sexe est obligatoire")
    private SexeEnum sexe;
}
