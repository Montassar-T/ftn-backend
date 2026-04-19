package com.carServices.backend.dtos;

import com.carServices.backend.enums.FuelLevel;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewRepairDto {
    private Long vehicleId;
    private Long mechanicId;

    private LocalDateTime scheduledAt;
    private LocalDateTime expectedDeliveryAt;

    private Integer mileage;
    private FuelLevel fuelLevel;

    private Boolean hasTools;
    private Boolean hasSpareWheel;
}
