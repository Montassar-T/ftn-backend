package com.ftn.backend.dtos.membership;

import com.ftn.backend.enums.ClubMembershipRoleEnum;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClubMembershipDto {
    private ClubMembershipRoleEnum role;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
}
