package com.ftn.backend.dtos.ranking;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateNationsRankingDto {
    @NotNull
    private Long athleteId;

    private Long eventId;
    private Long bestTimeMs;
    private BigDecimal pointsFina;
    private Integer rankPosition;
    private String season;
}
