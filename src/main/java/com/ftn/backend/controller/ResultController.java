package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.result.CreateResultDto;
import com.ftn.backend.dtos.result.ResultDto;
import com.ftn.backend.dtos.result.UpdateResultDto;
import com.ftn.backend.service.ResultService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/results")
@RequiredArgsConstructor
@Tag(name = "Result", description = "Results and rankings management APIs")
public class ResultController {

    private final ResultService resultService;

    @GetMapping
    public ResponseEntity<PageDto<ResultDto>> getAll(@RequestParam Map<String, String> params) {
        return resultService.getAll(params);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<ResultDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(resultService.getById(id)));
    }

    @GetMapping("/athlete/{athleteId}")
    public ResponseEntity<List<ResultDto>> getByAthlete(@PathVariable Long athleteId) {
        return ResponseEntity.ok(resultService.getByAthlete(athleteId));
    }

    @GetMapping("/competition/{competitionId}")
    public ResponseEntity<List<ResultDto>> getByCompetition(@PathVariable Long competitionId) {
        return ResponseEntity.ok(resultService.getByCompetition(competitionId));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<ResultDto>> create(@Valid @RequestBody CreateResultDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(resultService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<ResultDto>> update(@PathVariable Long id, @RequestBody UpdateResultDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(resultService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resultService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/valider")
    public ResponseEntity<SingleResultDto<ResultDto>> valider(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(resultService.valider(id)));
    }

    @PutMapping("/{id}/rejeter")
    public ResponseEntity<SingleResultDto<ResultDto>> rejeter(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(resultService.rejeter(id)));
    }
}
