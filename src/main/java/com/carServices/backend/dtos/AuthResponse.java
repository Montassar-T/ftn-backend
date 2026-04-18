package com.carServices.backend.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponse {
    private String email;
    private String firstName;
    private String lastName;
    private String accessToken;
}
