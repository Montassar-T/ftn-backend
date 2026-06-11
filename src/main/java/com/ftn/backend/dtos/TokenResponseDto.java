package com.ftn.backend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
public class TokenResponseDto {
    private String accessToken;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String refreshToken;
    private Long expiresIn;
}