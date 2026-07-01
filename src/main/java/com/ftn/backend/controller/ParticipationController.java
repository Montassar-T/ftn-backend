package com.ftn.backend.controller;

import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.participation.CreateParticipationDto;
import com.ftn.backend.dtos.participation.ParticipationDto;
import com.ftn.backend.service.ParticipationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Participations", description = "Event participation management APIs")
public class ParticipationController {

    private final ParticipationService participationService;

    @GetMapping("/evenements/{evenementId}/participations")
    public ResponseEntity<SingleResultDto<List<ParticipationDto>>> getByEvenement(@PathVariable Long evenementId) {
        return ResponseEntity.ok(new SingleResultDto<>(participationService.getByEvenement(evenementId)));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/evenements/{evenementId}/participations/me")
    public ResponseEntity<SingleResultDto<ParticipationDto>> getMyParticipation(
            @PathVariable Long evenementId, @RequestParam Long userId) {
        Optional<ParticipationDto> dto = participationService.getMyParticipation(evenementId, userId);
        return dto.map(d -> ResponseEntity.ok(new SingleResultDto<>(d)))
                .orElseGet(() -> ResponseEntity.ok(new SingleResultDto<>(null)));
    }

    @GetMapping("/users/{userId}/participations")
    public ResponseEntity<SingleResultDto<List<ParticipationDto>>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(new SingleResultDto<>(participationService.getByUser(userId)));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/participations")
    public ResponseEntity<SingleResultDto<ParticipationDto>> create(@Valid @RequestBody CreateParticipationDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(participationService.create(dto)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/participations/{id}/accept")
    public ResponseEntity<SingleResultDto<ParticipationDto>> accept(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(participationService.accept(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/participations/{id}/refuse")
    public ResponseEntity<SingleResultDto<ParticipationDto>> refuse(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(participationService.refuse(id)));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/participations/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        participationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
