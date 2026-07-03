package com.ftn.backend.controller;

import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.forum.CreateReactionDto;
import com.ftn.backend.dtos.forum.ForumReactionDto;
import com.ftn.backend.service.ForumReactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "ForumReactions", description = "Forum reaction APIs")
public class ForumReactionController {

    private final ForumReactionService reactionService;

    @PostMapping("/posts/{postId}/react")
    public ResponseEntity<SingleResultDto<ForumReactionDto>> reactToPost(
            @PathVariable Long postId, @Valid @RequestBody CreateReactionDto dto) {
        dto.setPostId(postId);
        return ResponseEntity.ok(new SingleResultDto<>(reactionService.react(dto)));
    }

    @PostMapping("/threads/{threadId}/react")
    public ResponseEntity<SingleResultDto<ForumReactionDto>> reactToThread(
            @PathVariable Long threadId, @Valid @RequestBody CreateReactionDto dto) {
        dto.setThreadId(threadId);
        return ResponseEntity.ok(new SingleResultDto<>(reactionService.react(dto)));
    }

    @DeleteMapping("/reactions/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sujets/{sujetId}/reactions")
    public ResponseEntity<List<ForumReactionDto>> getForSujet(@PathVariable Long sujetId) {
        return ResponseEntity.ok(reactionService.getForSujet(sujetId));
    }
}
