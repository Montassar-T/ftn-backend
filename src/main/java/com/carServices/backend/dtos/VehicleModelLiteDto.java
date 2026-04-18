package com.carServices.backend.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleModelLiteDto {
    private Long id;
    private String name;
}
