package com.ftn.backend.dtos.classement;

import com.ftn.backend.enums.CategorieEnum;
import com.ftn.backend.enums.DisciplineEnum;
import com.ftn.backend.enums.SexeEnum;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassementDto {
    private Long id;
    private Long athleteId;
    private DisciplineEnum discipline;
    private CategorieEnum categorie;
    private SexeEnum sexe;
    private Integer annee;
    private Integer pointsTotal;
    private Integer rang;
    private LocalDateTime createdAt;
}
