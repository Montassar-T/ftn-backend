package com.carServices.backend.dtos;

import lombok.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleModelDto {
    private Long id;
    private String name;
    private VehicleBrandDto brand;
}
