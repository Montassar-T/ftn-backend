package com.carServices.backend.dtos;

import java.util.List;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GlobalSearchDto {
    private List<ClientDto> clients;
    private List<VehicleDto> vehicles;
    private List<MechanicDto> mechanics;
}
