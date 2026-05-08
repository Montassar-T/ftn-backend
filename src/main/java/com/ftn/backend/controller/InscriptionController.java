package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.inscription.CreateInscriptionDto;
import com.ftn.backend.dtos.inscription.InscriptionDto;
import com.ftn.backend.service.InscriptionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inscriptions")
@RequiredArgsConstructor
@Tag(name = "Inscription", description = "Competition inscription APIs")
public class InscriptionController {

    private final InscriptionService inscriptionService;

    @GetMapping
    public ResponseEntity<PageDto<InscriptionDto>> getAll(@RequestParam Map<String, String> params) {
        return inscriptionService.getAll(params);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<InscriptionDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(inscriptionService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<InscriptionDto>> create(@Valid @RequestBody CreateInscriptionDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(inscriptionService.create(dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inscriptionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/valider")
    public ResponseEntity<SingleResultDto<InscriptionDto>> valider(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(inscriptionService.valider(id)));
    }

    @PutMapping("/{id}/annuler")
    public ResponseEntity<SingleResultDto<InscriptionDto>> annuler(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(inscriptionService.annuler(id)));
    }
}
