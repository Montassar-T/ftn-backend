package com.ftn.backend.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResult {

    private AuthResponse authResponse;
    private String refreshToken;
}
