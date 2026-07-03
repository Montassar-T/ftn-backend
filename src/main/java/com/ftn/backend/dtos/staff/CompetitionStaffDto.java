package com.ftn.backend.dtos.staff;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.PosteStaffEnum;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionStaffDto {

    private Long id;

    @JsonProperty("competition_id")
    private Long competitionId;

    @JsonProperty("competition_nom")
    private String competitionNom;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_nom")
    private String userNom;

    @JsonProperty("user_email")
    private String userEmail;

    private PosteStaffEnum poste;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
