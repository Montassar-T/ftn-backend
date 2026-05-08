package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.competition.CompetitionDto;
import com.ftn.backend.dtos.competition.CreateCompetitionDto;
import com.ftn.backend.dtos.competition.UpdateCompetitionDto;
import com.ftn.backend.service.CompetitionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/competitions")
@RequiredArgsConstructor
@Tag(name = "Competition", description = "Competition management APIs")
public class CompetitionController {

    private final CompetitionService competitionService;

    @GetMapping
    public ResponseEntity<PageDto<CompetitionDto>> getAll(@RequestParam Map<String, String> params) {
        return competitionService.getAll(params);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<CompetitionDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(competitionService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<CompetitionDto>> create(@Valid @RequestBody CreateCompetitionDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(competitionService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<CompetitionDto>> update(
            @PathVariable Long id, @RequestBody UpdateCompetitionDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(competitionService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        competitionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
