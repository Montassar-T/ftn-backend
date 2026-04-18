package com.carServices.backend.dtos;

import lombok.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleMakeDto {
    private Long id;
    private String name;
    private boolean systemAttribute;
}
