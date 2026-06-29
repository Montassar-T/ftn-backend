package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.ranking.CreateNationsRankingDto;
import com.ftn.backend.dtos.ranking.NationsRankingDto;
import com.ftn.backend.service.NationsRankingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/nations-rankings")
@RequiredArgsConstructor
@Tag(name = "NationsRanking", description = "National rankings APIs")
public class NationsRankingController {

    private final NationsRankingService rankingService;

    @GetMapping
    public ResponseEntity<PageDto<NationsRankingDto>> getAll(@RequestParam Map<String, String> params) {
        return rankingService.getAll(params);
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<NationsRankingDto>> create(@Valid @RequestBody CreateNationsRankingDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(rankingService.create(dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rankingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
