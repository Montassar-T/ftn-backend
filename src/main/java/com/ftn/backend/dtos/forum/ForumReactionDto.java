package com.ftn.backend.dtos.forum;

import com.ftn.backend.enums.ForumReactionTypeEnum;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForumReactionDto {
    private Long id;
    private Long threadId;
    private Long postId;
    private Long userId;
    private String userEmail;
    private ForumReactionTypeEnum type;
}
