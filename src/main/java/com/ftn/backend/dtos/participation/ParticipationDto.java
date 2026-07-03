package com.ftn.backend.dtos.participation;

import com.ftn.backend.enums.ParticipationStatus;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationDto {
    private Long id;
    private Long evenementId;
    private String evenementTitre;
    private Long userId;
    private String userName;
    private String message;
    private ParticipationStatus status;
    private LocalDateTime createdAt;
}
