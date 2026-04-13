package com.carServices.backend.dtos;

import com.carServices.backend.enums.MechanicStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewMechanicDto {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String phoneNumber;

    private String specialty;

    @NotNull
    private MechanicStatus status;
}
