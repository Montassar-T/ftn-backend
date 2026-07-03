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
public class NewsDto {
    private Long id;
    private String title;
    private String description;
    private String category;
    private LocalDateTime date;
    private AuthorDto author;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    private List<CommentDto> comments;
}
