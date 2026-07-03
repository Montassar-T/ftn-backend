package com.ftn.backend.dtos.pool;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ftn.backend.enums.PoolTypeEnum;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PoolDto {
    private Long id;
    private String nom;
    private String ville;
    private String adresse;
    private Integer longueur;

    @JsonProperty("nb_couloirs")
    private Integer nbCouloirs;

    private PoolTypeEnum type;
    private Boolean actif;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
