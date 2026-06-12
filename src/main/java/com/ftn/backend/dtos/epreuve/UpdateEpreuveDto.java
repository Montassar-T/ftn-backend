package com.ftn.backend.dtos.epreuve;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEpreuveDto {
    private String swimStyle;
    private String distance;
    private String gender;
    private String ageCategory;
    private String round;
    private LocalDate scheduledDate;
    private String status;
}
