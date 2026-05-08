package com.ftn.backend.dtos.epreuve;

import com.ftn.backend.enums.CategorieEnum;
import com.ftn.backend.enums.DisciplineEnum;
import com.ftn.backend.enums.SexeEnum;
import com.ftn.backend.enums.StyleEnum;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EpreuveDto {
    private Long id;
    private Long competitionId;
    private String nom;
    private DisciplineEnum discipline;
    private CategorieEnum categorie;
    private SexeEnum sexe;
    private Integer distance;
    private StyleEnum style;
    private LocalDateTime dateHeure;
    private LocalDateTime createdAt;
}
