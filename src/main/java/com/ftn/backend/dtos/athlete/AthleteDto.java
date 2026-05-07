package com.ftn.backend.dtos.athlete;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.CategorieEnum;
import com.ftn.backend.enums.SexeEnum;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AthleteDto {
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("club_id")
    private Long clubId;

    @JsonProperty("club_nom")
    private String clubNom;

    @JsonProperty("date_naissance")
    private LocalDate dateNaissance;

    private String nationalite;
    private CategorieEnum categorie;
    private SexeEnum sexe;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
