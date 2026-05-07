package com.ftn.backend.dtos;

import com.ftn.backend.enums.Role;
import com.ftn.backend.enums.UserStatus;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private Role role;

    private UserStatus status;
}
