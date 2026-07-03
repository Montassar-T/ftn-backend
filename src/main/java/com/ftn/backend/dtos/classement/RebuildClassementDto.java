package com.ftn.backend.dtos.classement;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RebuildClassementDto {

    @NotNull
    @JsonProperty("event_id")
    private Long eventId;

    @NotBlank
    private String season;
}
