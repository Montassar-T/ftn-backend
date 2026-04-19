package com.carServices.backend.dtos;

import com.carServices.backend.shared.Identifiable;
import lombok.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleMakeDto implements Identifiable {
    private Long id;
    private String name;
    private boolean systemAttribute;
}
