package com.ftn.backend.controller;

import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.record.CreateRecordDto;
import com.ftn.backend.dtos.record.RecordDto;
import com.ftn.backend.service.RecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
@Tag(name = "Records", description = "Swimming records APIs")
public class RecordController {

    private final RecordService recordService;

    @GetMapping
    public ResponseEntity<SingleResultDto<List<RecordDto>>> getAll() {
        return ResponseEntity.ok(new SingleResultDto<>(recordService.getAll()));
    }

    @GetMapping("/athlete/{athleteId}")
    public ResponseEntity<SingleResultDto<List<RecordDto>>> getByAthlete(@PathVariable Long athleteId) {
        return ResponseEntity.ok(new SingleResultDto<>(recordService.getByAthlete(athleteId)));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<RecordDto>> create(@Valid @RequestBody CreateRecordDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(recordService.create(dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recordService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
