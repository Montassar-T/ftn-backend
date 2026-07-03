package com.ftn.backend.dtos.schedule;

import com.ftn.backend.enums.PoolScheduleStatusEnum;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoolScheduleDto {
    private Long id;
    private Long poolId;
    private String poolNom;
    private String purpose;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private PoolScheduleStatusEnum status;
    private LocalDateTime createdAt;
}
