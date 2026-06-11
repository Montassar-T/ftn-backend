package com.ftn.backend.dtos.sujet;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSujetDto {
    private String titre;
    private String contenu;
}
