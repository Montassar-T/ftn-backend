package com.carServices.backend.dtos;

import com.carServices.backend.enums.MechanicStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MechanicDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String specialty;
    private MechanicStatus status;
}
