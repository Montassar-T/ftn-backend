package com.ftn.backend.dtos.record;

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
    private String type;
    private String swimStyle;
    private String distance;
    private Long athleteId;
    private Integer time;
    private LocalDate date;
    private Long competitionId;
    private LocalDateTime createdAt;
}
