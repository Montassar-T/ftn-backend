package com.ftn.backend.dtos.event;

import com.ftn.backend.enums.CategorieEnum;
import com.ftn.backend.enums.RoundTypeEnum;
import com.ftn.backend.enums.SexeEnum;
import com.ftn.backend.enums.SwimStyleEnum;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventDto {
    @NotNull
    private Long competitionId;

    private SwimStyleEnum swimStyle;
    private Integer distance;
    private SexeEnum gender;
    private CategorieEnum ageCategory;
    private RoundTypeEnum round;
    private LocalDate scheduledDate;
}
