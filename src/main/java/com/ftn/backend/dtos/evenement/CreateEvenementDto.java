package com.ftn.backend.dtos.evenement;

import com.ftn.backend.enums.EvenementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEvenementDto {

    @NotNull
    private EvenementType type;

    @NotBlank
    private String titre;

    private String description;

    @NotNull
    private LocalDateTime dateDebut;

    @NotNull
    private LocalDateTime dateFin;

    private String lieu;

    private Integer capaciteMax;

    private Long createdById;

    // ─── Champs spécifiques type=COMPETITION ────────────────────────────────
    private String competitionCode;
    /** Sous-type compétition : HIVER, ETE, OPEN, NATIONAL, INTERNATIONAL */
    private String competitionType;
    /** Taille de bassin : 25m ou 50m */
    private String lane;
    private String ageCategories;
    private LocalDate registrationDeadline;
}
