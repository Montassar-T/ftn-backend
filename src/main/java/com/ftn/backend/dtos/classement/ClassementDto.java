package com.ftn.backend.dtos.classement;

import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassementDto {
    private Long id;
    private Long athleteId;
    private String swimStyle;
    private String distance;
    private Integer bestTime;
    private Integer rank;
    private String season;
    private LocalDateTime createdAt;
}
