package com.ftn.backend.dtos.competition;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionDto {
    private Long id;
    private String code;
    private String name;
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime registrationDeadline;
    private Long poolId;
    private String lane;
    private String ageCategories;
    private String sourceUrl;
    private Long createdById;
    private String status;
    private LocalDateTime createdAt;
}
