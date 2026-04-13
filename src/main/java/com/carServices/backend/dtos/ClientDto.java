package com.carServices.backend.dtos;

import com.carServices.backend.enums.ClientType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDto {
    private Long id;

    private String phoneNumber;

    private String name;

    private ClientType type;

    private String representative;
}
