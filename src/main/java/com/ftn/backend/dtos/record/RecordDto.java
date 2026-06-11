package com.ftn.backend.dtos.record;

import com.ftn.backend.enums.RecordTypeEnum;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordDto {
    private Long id;
    private Long athleteId;
    private String athleteNom;
    private String athletePrenom;
    private Long eventId;
    private Long competitionId;
    private String competitionNom;
    private Long tempsMs;
    private String tempsDisplay;
    private LocalDate recordDate;
    private RecordTypeEnum type;
    private LocalDateTime createdAt;
}
