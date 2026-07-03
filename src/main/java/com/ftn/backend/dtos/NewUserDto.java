package com.ftn.backend.dtos;

import com.ftn.backend.enums.UserRole;
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

    private UserRole role;
}
