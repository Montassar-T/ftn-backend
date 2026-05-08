package com.ftn.backend.dtos.competition;

import com.ftn.backend.enums.DisciplineEnum;
import com.ftn.backend.enums.NiveauEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompetitionDto {

    @NotBlank
    private String nom;

    @NotNull
    private DisciplineEnum discipline;

    @NotNull
    private LocalDateTime dateDebut;

    @NotNull
    private LocalDateTime dateFin;

    @NotBlank
    private String lieu;

    private String region;

    @NotNull
    private NiveauEnum niveau;
}
