package com.ftn.backend.dtos.evenement;

import com.ftn.backend.enums.EvenementStatus;
import com.ftn.backend.enums.EvenementType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEvenementDto {
    private EvenementType type;
    private String titre;
    private String description;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String lieu;
    private Integer capaciteMax;
    private EvenementStatus status;

    // ─── Champs spécifiques type=COMPETITION ────────────────────────────────
    private String competitionCode;
    private String competitionType;
    private String lane;
    private String ageCategories;
    private LocalDate registrationDeadline;
}
