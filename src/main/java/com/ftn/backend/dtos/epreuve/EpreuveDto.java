package com.ftn.backend.dtos.epreuve;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EpreuveDto {
    private Long id;
    private Long competitionId;
    private String swimStyle;
    private String distance;
    private String gender;
    private String ageCategory;
    private String round;
    private LocalDate scheduledDate;
    private String status;
    private LocalDateTime createdAt;
}
