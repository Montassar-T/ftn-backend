package com.ftn.backend.dtos.membership;

import com.ftn.backend.enums.ClubMembershipRoleEnum;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateClubMembershipDto {
    @NotNull
    private Long userId;

    @NotNull
    private Long clubId;

    private ClubMembershipRoleEnum role;
    private LocalDate startDate;
    private LocalDate endDate;
}
