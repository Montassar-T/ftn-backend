package com.ftn.backend.dtos.sujet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.CategorieForumEnum;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SujetDto {
    private Long id;

    @JsonProperty("forum_id")
    private Long forumId;

    @JsonProperty("forum_nom")
    private String forumNom;

    @JsonProperty("forum_categorie")
    private CategorieForumEnum forumCategorie;

    @JsonProperty("auteur_id")
    private Long auteurId;

    @JsonProperty("auteur_nom")
    private String auteurNom;

    @JsonProperty("auteur_prenom")
    private String auteurPrenom;

    @JsonProperty("auteur_email")
    private String auteurEmail;

    private String titre;
    private String contenu;

    @JsonProperty("date_creation")
    private LocalDateTime dateCreation;

    private Boolean epingle;
    private Boolean ferme;

    @JsonProperty("nb_vues")
    private Integer nbVues;

    @JsonProperty("nb_reponses")
    private Integer nbReponses;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
