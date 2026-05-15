package com.ftn.backend.dtos.sujet;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("auteur_id")
    private Long auteurId;

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

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
