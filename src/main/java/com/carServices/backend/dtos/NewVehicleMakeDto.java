package com.carServices.backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewVehicleMakeDto {
    @NotNull
    private String name;
}
