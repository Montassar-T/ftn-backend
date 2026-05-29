package com.ftn.backend.dtos;

import lombok.*;

@Data
@Builder
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
}