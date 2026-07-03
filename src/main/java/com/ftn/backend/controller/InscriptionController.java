package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.inscription.CreateInscriptionDto;
import com.ftn.backend.dtos.inscription.InscriptionDto;
import com.ftn.backend.service.InscriptionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/registrations")
@RequiredArgsConstructor
@Tag(name = "Registration", description = "Event registration APIs")
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

    @GetMapping("/athlete/{athleteId}")
    public ResponseEntity<List<InscriptionDto>> getByAthlete(@PathVariable Long athleteId) {
        return ResponseEntity.ok(inscriptionService.getByAthlete(athleteId));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<InscriptionDto>> getByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(inscriptionService.getByEvent(eventId));
    }

    @GetMapping("/competition/{competitionId}")
    public ResponseEntity<List<InscriptionDto>> getByCompetition(@PathVariable Long competitionId) {
        return ResponseEntity.ok(inscriptionService.getByCompetition(competitionId));
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

    @PutMapping("/{id}/validate")
    public ResponseEntity<SingleResultDto<InscriptionDto>> validate(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(inscriptionService.valider(id)));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<SingleResultDto<InscriptionDto>> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(inscriptionService.annuler(id)));
    }
}
