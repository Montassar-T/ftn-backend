package com.ftn.backend.dtos.schedule;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePoolScheduleDto {
    @NotNull
    private Long poolId;

    private String purpose;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
