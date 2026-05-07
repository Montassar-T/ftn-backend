package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.sujet.CreateSujetDto;
import com.ftn.backend.dtos.sujet.SujetDto;
import com.ftn.backend.dtos.sujet.UpdateSujetDto;
import com.ftn.backend.service.SujetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sujets")
@RequiredArgsConstructor
@Tag(name = "Sujet", description = "Forum topic management APIs")
public class SujetController {

    private final SujetService sujetService;

    @GetMapping
    public ResponseEntity<PageDto<SujetDto>> getAll(@RequestParam Map<String, String> params) {
        return sujetService.getAll(params);
    }

    @GetMapping("/forum/{forumId}")
    public ResponseEntity<List<SujetDto>> getByForum(@PathVariable Long forumId) {
        return ResponseEntity.ok(sujetService.getByForum(forumId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<SujetDto>> getById(@PathVariable Long id) {
        sujetService.incrementerVues(id);
        return ResponseEntity.ok(new SingleResultDto<>(sujetService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<SujetDto>> create(@RequestBody CreateSujetDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(sujetService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<SujetDto>> update(
            @PathVariable Long id, @RequestBody UpdateSujetDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(sujetService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sujetService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/epingler")
    public ResponseEntity<SingleResultDto<SujetDto>> epingler(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(sujetService.epingler(id)));
    }

    @PutMapping("/{id}/fermer")
    public ResponseEntity<SingleResultDto<SujetDto>> fermer(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(sujetService.fermer(id)));
    }
}
