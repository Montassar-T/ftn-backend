package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.record.CreateRecordDto;
import com.ftn.backend.dtos.record.RecordDto;
import com.ftn.backend.service.RecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
@Tag(name = "Record", description = "Record management APIs")
public class RecordController {

    private final RecordService recordService;

    @GetMapping
    public ResponseEntity<PageDto<RecordDto>> getAll(@RequestParam Map<String, String> params) {
        return recordService.getAll(params);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<RecordDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(recordService.getById(id)));
    }

    @GetMapping("/athlete/{athleteId}")
    public ResponseEntity<List<RecordDto>> getByAthlete(@PathVariable Long athleteId) {
        return ResponseEntity.ok(recordService.getByAthlete(athleteId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecordDto>> getBySwimStyleAndDistance(
            @RequestParam String swimStyle, @RequestParam String distance) {
        return ResponseEntity.ok(recordService.getByEvent(swimStyle, distance));
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
