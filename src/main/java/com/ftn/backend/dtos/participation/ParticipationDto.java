package com.ftn.backend.dtos.participation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.ParticipationStatusEnum;
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
    private String userEmail;
    private String message;
    private ParticipationStatusEnum status;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("queue_position")
    private Integer queuePosition;

    @JsonProperty("user_name")
    private String userName;
}
