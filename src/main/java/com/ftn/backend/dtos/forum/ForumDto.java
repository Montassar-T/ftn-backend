package com.ftn.backend.dtos.forum;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.CategorieForumEnum;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForumDto {
    private Long id;
    private String nom;
    private String description;
    private CategorieForumEnum categorie;

    @JsonProperty("nb_sujets")
    private Integer nbSujets;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
