package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.evenement.CreateEvenementDto;
import com.ftn.backend.dtos.evenement.EvenementDto;
import com.ftn.backend.dtos.evenement.UpdateEvenementDto;
import com.ftn.backend.dtos.participation.CreateParticipationDto;
import com.ftn.backend.dtos.participation.ParticipationDto;
import com.ftn.backend.enums.ParticipationStatus;
import com.ftn.backend.service.EvenementService;
import com.ftn.backend.service.ParticipationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/evenements")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Tag(name = "Evenement", description = "Event management APIs")
public class EvenementController {

    private final EvenementService evenementService;
    private final ParticipationService participationService;

    @GetMapping
    public ResponseEntity<PageDto<EvenementDto>> getAll(@RequestParam Map<String, String> params) {
        return evenementService.getAll(params);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<EvenementDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(evenementService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<EvenementDto>> create(@Valid @RequestBody CreateEvenementDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(evenementService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<EvenementDto>> update(
            @PathVariable Long id, @RequestBody UpdateEvenementDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(evenementService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        evenementService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Participation sub-resource
    @GetMapping("/{id}/participations")
    public ResponseEntity<List<ParticipationDto>> getParticipations(@PathVariable Long id) {
        return ResponseEntity.ok(participationService.getByEvenement(id));
    }

    @PostMapping("/{id}/participations")
    public ResponseEntity<SingleResultDto<ParticipationDto>> register(
            @PathVariable Long id, @Valid @RequestBody CreateParticipationDto dto) {
        dto.setEvenementId(id);
        return ResponseEntity.ok(new SingleResultDto<>(participationService.create(dto)));
    }

    @PatchMapping("/participations/{participationId}/status")
    public ResponseEntity<SingleResultDto<ParticipationDto>> updateParticipationStatus(
            @PathVariable Long participationId, @RequestParam ParticipationStatus status) {
        return ResponseEntity.ok(new SingleResultDto<>(participationService.updateStatus(participationId, status)));
    }

    @DeleteMapping("/participations/{participationId}")
    public ResponseEntity<Void> deleteParticipation(@PathVariable Long participationId) {
        participationService.delete(participationId);
        return ResponseEntity.noContent().build();
    }
}
