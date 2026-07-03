package com.ftn.backend.dtos.resultat;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateResultatDto {

    @NotNull
    @JsonProperty("athlete_id")
    private Long athleteId;

    @NotNull
    @JsonProperty("event_id")
    private Long eventId;

    private Integer lane;

    @JsonProperty("temps_ms")
    private Integer tempsMs;

    @JsonProperty("temps_display")
    private String tempsDisplay;

    @JsonProperty("points_fina")
    private BigDecimal pointsFina;

    private String tour;

    @NotNull
    private Integer rank;

    @JsonProperty("is_record")
    private Boolean isRecord;

    @JsonProperty("validated_by_id")
    private Long validatedById;
}
