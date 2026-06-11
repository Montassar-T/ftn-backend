package com.ftn.backend.dtos.competition;

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

    private String code;

    @NotBlank
    private String name;

    @NotBlank
    private String type;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private LocalDateTime registrationDeadline;

    private Long poolId;

    private String lane;

    private String ageCategories;

    private String sourceUrl;

    private Long createdById;
}
