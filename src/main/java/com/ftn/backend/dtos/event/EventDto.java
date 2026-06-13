package com.ftn.backend.dtos.event;

import com.ftn.backend.enums.CategorieEnum;
import com.ftn.backend.enums.EventStatusEnum;
import com.ftn.backend.enums.RoundTypeEnum;
import com.ftn.backend.enums.SexeEnum;
import com.ftn.backend.enums.SwimStyleEnum;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private Long id;
    private Long competitionId;
    private String competitionNom;
    private SwimStyleEnum swimStyle;
    private Integer distance;
    private SexeEnum gender;
    private CategorieEnum ageCategory;
    private RoundTypeEnum round;
    private LocalDate scheduledDate;
    private EventStatusEnum status;
    private LocalDateTime createdAt;
}
