package com.carServices.backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
public class UpdateUserDto {
    @NotNull
    private String firstName;

    @NotNull
    private String lastName;
}
