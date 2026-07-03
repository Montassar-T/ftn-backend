package com.ftn.backend.dtos.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReponseDto {
    private String contenu;

    @JsonProperty("image_url")
    private String imageUrl;
}
