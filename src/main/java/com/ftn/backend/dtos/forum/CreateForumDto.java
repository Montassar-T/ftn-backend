package com.ftn.backend.dtos.forum;

import com.ftn.backend.enums.CategorieForumEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateForumDto {

    @NotBlank(message = "Le nom du forum est obligatoire")
    private String nom;

    private String description;

    @NotNull(message = "La catégorie est obligatoire")
    private CategorieForumEnum categorie;
}
