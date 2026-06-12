package com.ftn.backend.controller;

import com.ftn.backend.dtos.*;
import com.ftn.backend.service.NewsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
@Tag(name = "News", description = "News APIs")
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<PageDto<NewsDto>> getAllNews(@RequestParam Map<String, String> params) {
        return newsService.getAllNews(params);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<NewsDto>> getNewsById(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.getNewsById(id));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<NewsDto>> createNews(
            @RequestBody CreateNewsDto dto, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : "anonymousUser";
        NewsDto news = newsService.createNews(dto, email);
        return ResponseEntity.ok(new SingleResultDto<>(news));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<SingleResultDto<CommentDto>> addComment(
            @PathVariable Long id, @RequestBody CreateCommentDto dto, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : "anonymousUser";
        CommentDto comment = newsService.addComment(id, dto, email);
        return ResponseEntity.ok(new SingleResultDto<>(comment));
    }
}
