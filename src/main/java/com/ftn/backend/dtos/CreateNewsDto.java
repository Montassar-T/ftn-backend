package com.ftn.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateNewsDto {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String category;
}
