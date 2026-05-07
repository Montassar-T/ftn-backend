package com.ftn.backend.dtos.equipe;

import com.ftn.backend.enums.CategorieEnum;
import com.ftn.backend.enums.DisciplineEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEquipeNationaleDto {
    private DisciplineEnum discipline;
    private CategorieEnum categorie;
    private Integer annee;
}
