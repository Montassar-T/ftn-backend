package com.ftn.backend.dtos.club;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClubStaffDto {
    private Long id;
    private Long clubId;
    private String clubNom;
    private Long userId;
    private String userNom;
    private String userEmail;
    private String poste;
}
