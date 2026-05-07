package com.ftn.backend.dtos.equipe;

import com.ftn.backend.enums.CategorieEnum;
import com.ftn.backend.enums.DisciplineEnum;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipeNationaleDto {
    private Long id;
    private DisciplineEnum discipline;
    private CategorieEnum categorie;
    private Integer annee;

    @JsonProperty("membres_ids")
    private List<Long> membresIds;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
