package com.ftn.backend.dtos.competition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.CompetitionTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompetitionDto {

    // --- HEAD fields ---
    private CompetitionTypeEnum type;

    @JsonProperty("pool_id")
    private Long poolId;

    private String description;

    // --- Upstream fields ---
    private String code;

    @NotBlank
    private String name;

    private String typeStr;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private LocalDateTime registrationDeadline;

    private String lane;

    private String ageCategories;

    private String sourceUrl;

    private Long createdById;
}
