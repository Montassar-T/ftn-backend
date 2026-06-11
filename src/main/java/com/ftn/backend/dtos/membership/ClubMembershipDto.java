package com.ftn.backend.dtos.membership;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubMembershipDto {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    private Long clubId;
    private String clubNom;
    private String role;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
    private LocalDateTime createdAt;
}
