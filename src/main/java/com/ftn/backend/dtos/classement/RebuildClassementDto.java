package com.ftn.backend.dtos.classement;

import com.ftn.backend.enums.CategorieEnum;
import com.ftn.backend.enums.DisciplineEnum;
import com.ftn.backend.enums.SexeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RebuildClassementDto {

    @NotNull
    private DisciplineEnum discipline;

    @NotNull
    private CategorieEnum categorie;

    @NotNull
    private SexeEnum sexe;

    @NotNull
    private Integer annee;
}
