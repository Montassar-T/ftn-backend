package com.ftn.backend.dtos.inscription;

import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionDto {
    private Long id;
    private Long athleteId;
    private String athleteName;
    private String clubName;
    private Long eventId;
    private String eventLabel;
    private Long competitionId;
    private String competitionName;
    private String seedTime;
    private String status;
    private LocalDateTime registeredAt;
    private LocalDateTime createdAt;
}
