package com.ftn.backend.dtos.record;

import com.ftn.backend.enums.RecordTypeEnum;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecordDto {
    private Long id;

    // --- HEAD fields ---
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

    // --- Upstream fields ---
    private String typeStr;
    private String swimStyle;
    private String distance;
    private Integer time;
    private LocalDate date;

    private LocalDateTime createdAt;
}
