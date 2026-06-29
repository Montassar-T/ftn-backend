package com.ftn.backend.dtos;

import com.ftn.backend.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private String accessToken;
}
