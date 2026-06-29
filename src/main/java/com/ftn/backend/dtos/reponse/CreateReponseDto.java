package com.ftn.backend.dtos.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReponseDto {

    @NotNull(message = "Le sujet est obligatoire")
    @JsonProperty("sujet_id")
    private Long sujetId;

    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;

    @JsonProperty("image_url")
    private String imageUrl;
}
