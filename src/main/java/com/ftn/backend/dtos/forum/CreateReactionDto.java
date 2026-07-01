package com.ftn.backend.dtos.forum;

import com.ftn.backend.enums.ForumReactionTypeEnum;
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
    private ForumReactionTypeEnum type;
}
