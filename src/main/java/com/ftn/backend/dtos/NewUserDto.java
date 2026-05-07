package com.carServices.backend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
public class NewUserDto {
    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String email;

    @NotNull
    private String password;
}
