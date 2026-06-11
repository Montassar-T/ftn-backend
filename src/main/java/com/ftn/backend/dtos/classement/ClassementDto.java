package com.ftn.backend.dtos.classement;

import java.math.BigDecimal;
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
    private String athleteName;
    private String athleteNationality;
    private Long clubId;
    private String clubName;
    private Long eventId;
    private String swimStyle;
    private String distance;
    private String gender;
    private String ageCategory;
    private Integer bestTimeMs;
    private String bestTimeDisplay;
    private BigDecimal pointsFina;
    private Integer rank;
    private String season;
    private LocalDateTime createdAt;
}
