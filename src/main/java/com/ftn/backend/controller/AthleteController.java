package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.athlete.AthleteDto;
import com.ftn.backend.dtos.athlete.CreateAthleteDto;
import com.ftn.backend.dtos.athlete.UpdateAthleteDto;
import com.ftn.backend.dtos.licence.LicenceDto;
import com.ftn.backend.service.AthleteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/athletes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Tag(name = "Athlete", description = "Athlete management APIs")
public class AthleteController {

    private final AthleteService athleteService;

    @GetMapping
    public ResponseEntity<PageDto<AthleteDto>> getAll(@RequestParam Map<String, String> params) {
        return athleteService.getAll(params);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<AthleteDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(athleteService.getById(id)));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<SingleResultDto<AthleteDto>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(new SingleResultDto<>(athleteService.getByUserId(userId)));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<AthleteDto>> create(@Valid @RequestBody CreateAthleteDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(athleteService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<AthleteDto>> update(
            @PathVariable Long id, @RequestBody UpdateAthleteDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(athleteService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        athleteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/licences")
    public ResponseEntity<List<LicenceDto>> getLicences(@PathVariable Long id) {
        return ResponseEntity.ok(athleteService.getLicences(id));
    }
}
