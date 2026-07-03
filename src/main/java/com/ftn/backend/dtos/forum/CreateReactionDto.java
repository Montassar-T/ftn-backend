package com.ftn.backend.dtos.forum;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReactionDto {
    private Long threadId;
    private Long postId;

    @NotNull
    private Long userId;

    @NotNull
    private String type;
}
