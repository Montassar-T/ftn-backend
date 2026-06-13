package com.ftn.backend.dtos.forum;

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
    private String type;
}
