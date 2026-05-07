package com.ftn.backend.dtos.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReponseDto {

    @JsonProperty("sujet_id")
    private Long sujetId;

    private String contenu;
}
