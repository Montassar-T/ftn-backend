package com.ftn.backend.dtos.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRecordDto {

    @NotBlank
    private String type;

    @NotBlank
    private String swimStyle;

    @NotBlank
    private String distance;

    @NotNull
    private Long athleteId;

    @NotNull
    private Integer time;

    @NotNull
    private LocalDate date;

    private Long competitionId;
}
