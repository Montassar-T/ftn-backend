package com.ftn.backend.dtos.actualite;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.CategorieActuEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateActualiteDto {
    private String titre;
    private String contenu;
    private CategorieActuEnum categorie;

    @JsonProperty("image_url")
    private String imageUrl;
}
