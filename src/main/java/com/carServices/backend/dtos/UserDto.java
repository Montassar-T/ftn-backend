package com.carServices.backend.dtos;

import com.carServices.backend.enums.Role;
import com.carServices.backend.model.UserStatus;
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
