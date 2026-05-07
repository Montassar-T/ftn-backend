package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.equipe.CreateEquipeNationaleDto;
import com.ftn.backend.dtos.equipe.EquipeNationaleDto;
import com.ftn.backend.dtos.equipe.UpdateEquipeNationaleDto;
import com.ftn.backend.service.EquipeNationaleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/equipes-nationales")
@RequiredArgsConstructor
@Tag(name = "EquipeNationale", description = "National team management APIs")
public class EquipeNationaleController {

    private final EquipeNationaleService equipeService;

    @GetMapping
    public ResponseEntity<PageDto<EquipeNationaleDto>> getAll(
            @RequestParam Map<String, String> params) {
        return equipeService.getAll(params);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<EquipeNationaleDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(equipeService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<EquipeNationaleDto>> create(
            @RequestBody CreateEquipeNationaleDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(equipeService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<EquipeNationaleDto>> update(
            @PathVariable Long id, @RequestBody UpdateEquipeNationaleDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(equipeService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        equipeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/membres/{athleteId}")
    public ResponseEntity<SingleResultDto<EquipeNationaleDto>> addMembre(
            @PathVariable Long id, @PathVariable Long athleteId) {
        return ResponseEntity.ok(new SingleResultDto<>(equipeService.addMembre(id, athleteId)));
    }

    @DeleteMapping("/{id}/membres/{athleteId}")
    public ResponseEntity<SingleResultDto<EquipeNationaleDto>> removeMembre(
            @PathVariable Long id, @PathVariable Long athleteId) {
        return ResponseEntity.ok(new SingleResultDto<>(equipeService.removeMembre(id, athleteId)));
    }
}
