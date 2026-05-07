package com.carServices.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ResetPasswordDto {
    @NotBlank
    private String password;
}
