package com.ftn.backend.dtos.epreuve;

import com.ftn.backend.enums.CategorieEnum;
import com.ftn.backend.enums.DisciplineEnum;
import com.ftn.backend.enums.SexeEnum;
import com.ftn.backend.enums.StyleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEpreuveDto {

    @NotNull
    private Long competitionId;

    @NotBlank
    private String nom;

    @NotNull
    private DisciplineEnum discipline;

    @NotNull
    private CategorieEnum categorie;

    @NotNull
    private SexeEnum sexe;

    @NotNull
    private Integer distance;

    @NotNull
    private StyleEnum style;

    @NotNull
    private LocalDateTime dateHeure;
}
