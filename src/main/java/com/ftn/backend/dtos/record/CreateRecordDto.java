package com.ftn.backend.dtos.record;

import com.ftn.backend.enums.RecordTypeEnum;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecordDto {
    @NotNull
    private Long athleteId;

    private Long eventId;
    private Long competitionId;
    private Long tempsMs;
    private String tempsDisplay;
    private LocalDate recordDate;
    private RecordTypeEnum type;
}
