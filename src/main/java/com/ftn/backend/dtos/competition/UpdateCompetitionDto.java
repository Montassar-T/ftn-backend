package com.ftn.backend.dtos.competition;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompetitionDto {
    private String name;
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime registrationDeadline;
    private Long poolId;
    private Long createdById;
    private String status;
}
