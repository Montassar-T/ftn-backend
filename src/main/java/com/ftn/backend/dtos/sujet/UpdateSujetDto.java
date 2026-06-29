package com.ftn.backend.dtos.sujet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSujetDto {
    private String titre;
    private String contenu;

    @JsonProperty("image_url")
    private String imageUrl;
}
