package com.carServices.backend.dtos;

import com.carServices.backend.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDto {

    private Long id;

    private String registration;

    private Integer mileage;

    private String vin;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.TIMESTAMP_FORMAT)
    private LocalDateTime registrationDate;

    private VehicleModelDto model;
}
