package com.ftn.backend.dtos.forum;

import com.ftn.backend.enums.CategorieForumEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateForumDto {
    private String nom;
    private String description;
    private CategorieForumEnum categorie;
}
