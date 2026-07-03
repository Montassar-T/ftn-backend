package com.ftn.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private String fileUrl;
    private AuthorDto author;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    private List<CommentDto> replies;
}
