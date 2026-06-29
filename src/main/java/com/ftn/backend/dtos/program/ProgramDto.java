package com.ftn.backend.dtos.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgramDto {
    private Long id;
    private String nom;
    private String description;

    @JsonProperty("age_min")
    private Integer ageMin;

    @JsonProperty("age_max")
    private Integer ageMax;

    @JsonProperty("image_url")
    private String imageUrl;

    private Boolean actif;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
