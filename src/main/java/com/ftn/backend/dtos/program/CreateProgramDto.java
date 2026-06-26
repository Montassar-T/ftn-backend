package com.ftn.backend.dtos.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProgramDto {

    @NotBlank(message = "Le nom du programme est obligatoire")
    private String nom;

    private String description;

    @JsonProperty("age_min")
    private Integer ageMin;

    @JsonProperty("age_max")
    private Integer ageMax;

    @JsonProperty("image_url")
    private String imageUrl;

    private Boolean actif;
}
