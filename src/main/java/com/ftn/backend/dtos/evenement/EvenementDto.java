package com.ftn.backend.dtos.evenement;

import com.ftn.backend.enums.EvenementStatus;
import com.ftn.backend.enums.EvenementType;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvenementDto {
    private Long id;
    private EvenementType type;
    private String titre;
    private String description;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String lieu;
    private Integer capaciteMax;
    private EvenementStatus status;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;

    /** ID de la compétition liée (non null quand type=COMPETITION) */
    private Long competitionId;
}
