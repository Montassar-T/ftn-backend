package com.ftn.backend.dtos.equipe;

import com.ftn.backend.enums.CategorieEnum;
import com.ftn.backend.enums.DisciplineEnum;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEquipeNationaleDto {

    @NotNull(message = "La discipline est obligatoire")
    private DisciplineEnum discipline;

    @NotNull(message = "La catégorie est obligatoire")
    private CategorieEnum categorie;

    @NotNull(message = "L'année est obligatoire")
    private Integer annee;
}
