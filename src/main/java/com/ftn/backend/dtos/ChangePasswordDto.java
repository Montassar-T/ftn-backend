package com.ftn.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChangePasswordDto {
    @NotBlank
    private String currentPassword;

    @NotBlank
    private String newPassword;
}
