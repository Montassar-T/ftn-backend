package com.ftn.backend.dtos.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReponseDto {
    private Long id;

    @JsonProperty("sujet_id")
    private Long sujetId;

    @JsonProperty("auteur_id")
    private Long auteurId;

    @JsonProperty("auteur_nom")
    private String auteurNom;

    @JsonProperty("auteur_prenom")
    private String auteurPrenom;

    @JsonProperty("auteur_email")
    private String auteurEmail;

    private String contenu;

    @JsonProperty("date_creation")
    private LocalDateTime dateCreation;

    @JsonProperty("nb_likes")
    private Integer nbLikes;

    private Boolean signale;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
