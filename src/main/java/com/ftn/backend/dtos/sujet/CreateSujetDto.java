package com.ftn.backend.dtos.sujet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSujetDto {

    @JsonProperty("forum_id")
    private Long forumId;

    private String titre;
    private String contenu;
}
