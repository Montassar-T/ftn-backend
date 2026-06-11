package com.ftn.backend.dtos.membership;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClubMembershipDto {
    private String role;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
}
