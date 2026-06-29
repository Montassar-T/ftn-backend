package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.program.CreateProgramDto;
import com.ftn.backend.dtos.program.ProgramDto;
import com.ftn.backend.dtos.program.UpdateProgramDto;
import com.ftn.backend.service.ProgramService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/programs")
@RequiredArgsConstructor
@Tag(name = "Program", description = "Training program management APIs")
public class ProgramController {

    private final ProgramService programService;

    @GetMapping
    public ResponseEntity<PageDto<ProgramDto>> getAll(@RequestParam Map<String, String> params) {
        return programService.getAll(params);
    }

    @GetMapping("/actives")
    public ResponseEntity<List<ProgramDto>> getActives() {
        return ResponseEntity.ok(programService.getActives());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<ProgramDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(programService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<ProgramDto>> create(@Valid @RequestBody CreateProgramDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(programService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<ProgramDto>> update(
            @PathVariable Long id, @RequestBody UpdateProgramDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(programService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        programService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
