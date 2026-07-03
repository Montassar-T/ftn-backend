package com.ftn.backend.dtos.record;

import com.ftn.backend.enums.RecordTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecordDto {

    // --- HEAD fields ---
    @NotNull
    private Long athleteId;

    private Long eventId;
    private Long competitionId;
    private Long tempsMs;
    private String tempsDisplay;
    private LocalDate recordDate;
    private RecordTypeEnum type;

    // --- Upstream fields ---
    @NotBlank
    private String typeStr;

    @NotBlank
    private String swimStyle;

    @NotBlank
    private String distance;

    @NotNull
    private Integer time;

    @NotNull
    private LocalDate date;
}
