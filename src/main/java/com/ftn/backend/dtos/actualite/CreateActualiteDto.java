package com.ftn.backend.dtos.actualite;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.CategorieActuEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateActualiteDto {

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;

    @NotNull(message = "La catégorie est obligatoire")
    private CategorieActuEnum categorie;

    @JsonProperty("image_url")
    private String imageUrl;
}
