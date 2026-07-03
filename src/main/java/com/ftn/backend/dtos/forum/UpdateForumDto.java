package com.ftn.backend.dtos.forum;

import com.ftn.backend.enums.CategorieForumEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateForumDto {
    private String nom;
    private String description;
    private CategorieForumEnum categorie;
}
