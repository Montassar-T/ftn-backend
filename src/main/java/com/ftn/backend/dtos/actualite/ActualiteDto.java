package com.ftn.backend.dtos.actualite;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.CategorieActuEnum;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActualiteDto {
    private Long id;

    @JsonProperty("auteur_id")
    private Long auteurId;

    private String titre;
    private String contenu;
    private CategorieActuEnum categorie;

    @JsonProperty("date_publication")
    private LocalDateTime datePublication;

    private Boolean publie;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
