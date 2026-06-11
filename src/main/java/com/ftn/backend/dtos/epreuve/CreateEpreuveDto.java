package com.ftn.backend.dtos.epreuve;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEpreuveDto {

    @NotNull
    private Long competitionId;

    @NotBlank
    private String swimStyle;

    @NotBlank
    private String distance;

    @NotBlank
    private String gender;

    private String ageCategory;

    private String round;

    @NotNull
    private LocalDate scheduledDate;
}
