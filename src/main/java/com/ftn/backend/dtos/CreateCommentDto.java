package com.ftn.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentDto {

    @NotBlank
    private String content;

    private Long parentCommentId;

    private String fileUrl;
}
