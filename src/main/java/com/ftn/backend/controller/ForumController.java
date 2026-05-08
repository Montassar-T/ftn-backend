package com.ftn.backend.controller;

import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.forum.CreateForumDto;
import com.ftn.backend.dtos.forum.ForumDto;
import com.ftn.backend.dtos.forum.UpdateForumDto;
import com.ftn.backend.service.ForumService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/forums")
@RequiredArgsConstructor
@Tag(name = "Forum", description = "Forum management APIs")
public class ForumController {

    private final ForumService forumService;

    @GetMapping
    public ResponseEntity<List<ForumDto>> getAll() {
        return ResponseEntity.ok(forumService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<ForumDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(forumService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FORUM_CREATE')")
    public ResponseEntity<SingleResultDto<ForumDto>> create(@Valid @RequestBody CreateForumDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(forumService.create(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FORUM_UPDATE')")
    public ResponseEntity<SingleResultDto<ForumDto>> update(@PathVariable Long id, @RequestBody UpdateForumDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(forumService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FORUM_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        forumService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
