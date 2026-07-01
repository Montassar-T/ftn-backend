package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.epreuve.CreateEpreuveDto;
import com.ftn.backend.dtos.epreuve.EpreuveDto;
import com.ftn.backend.dtos.epreuve.UpdateEpreuveDto;
import com.ftn.backend.service.EpreuveService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/epreuves")
@RequiredArgsConstructor
@Tag(name = "Event", description = "Event management APIs")
public class EpreuveController {

    private final EpreuveService epreuveService;

    @GetMapping
    public ResponseEntity<PageDto<EpreuveDto>> getAll(@RequestParam Map<String, String> params) {
        return epreuveService.getAll(params);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<EpreuveDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(epreuveService.getById(id)));
    }

    @GetMapping("/competition/{competitionId}")
    public ResponseEntity<List<EpreuveDto>> getByCompetition(@PathVariable Long competitionId) {
        return ResponseEntity.ok(epreuveService.getByCompetition(competitionId));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<EpreuveDto>> create(@Valid @RequestBody CreateEpreuveDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(epreuveService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<EpreuveDto>> update(
            @PathVariable Long id, @RequestBody UpdateEpreuveDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(epreuveService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        epreuveService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
