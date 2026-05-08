package com.ftn.backend.dtos.competition;

import com.ftn.backend.enums.DisciplineEnum;
import com.ftn.backend.enums.NiveauEnum;
import com.ftn.backend.enums.StatutCompEnum;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompetitionDto {
    private String nom;
    private DisciplineEnum discipline;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String lieu;
    private String region;
    private NiveauEnum niveau;
    private StatutCompEnum statut;
}
