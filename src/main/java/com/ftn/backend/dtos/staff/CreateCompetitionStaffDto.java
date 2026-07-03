package com.ftn.backend.dtos.staff;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.PosteStaffEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCompetitionStaffDto {

    @JsonProperty("user_id")
    private Long userId;

    private PosteStaffEnum poste;
}
