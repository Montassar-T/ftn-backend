package com.carServices.backend.dtos;

import com.carServices.backend.enums.ActivityLogAction;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLogDto {
    private Long id;

    private ActivityLogAction action;

    private String entityType;

    private Long entityId;

    private UserDto user;

    private String metadata;
}
