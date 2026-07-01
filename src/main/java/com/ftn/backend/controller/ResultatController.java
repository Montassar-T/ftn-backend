package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.resultat.AthleteStatsDto;
import com.ftn.backend.dtos.resultat.CreateResultatDto;
import com.ftn.backend.dtos.resultat.ResultatDto;
import com.ftn.backend.dtos.resultat.UpdateResultatDto;
import com.ftn.backend.service.ResultatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/resultats")
@RequiredArgsConstructor
@Tag(name = "Resultat", description = "Résultats de compétition")
public class ResultatController {

    private final ResultatService resultatService;

    @GetMapping
    public ResponseEntity<PageDto<ResultatDto>> getAll(@RequestParam Map<String, String> params) {
        return resultatService.getAll(params);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<ResultatDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(resultatService.getById(id)));
    }

    @GetMapping("/athlete/{athleteId}")
    public ResponseEntity<List<ResultatDto>> getByAthlete(@PathVariable Long athleteId) {
        return ResponseEntity.ok(resultatService.getByAthlete(athleteId));
    }

    @GetMapping("/athlete/{athleteId}/stats")
    public ResponseEntity<AthleteStatsDto> getAthleteStats(@PathVariable Long athleteId) {
        return ResponseEntity.ok(resultatService.getAthleteStats(athleteId));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<ResultatDto>> getByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(resultatService.getByEvent(eventId));
    }

    @GetMapping("/competition/{competitionId}")
    public ResponseEntity<List<ResultatDto>> getByCompetition(@PathVariable Long competitionId) {
        return ResponseEntity.ok(resultatService.getByCompetition(competitionId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COACH')")
    @PostMapping
    public ResponseEntity<SingleResultDto<ResultatDto>> create(@Valid @RequestBody CreateResultatDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(resultatService.create(dto)));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COACH')")
    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<ResultatDto>> update(
            @PathVariable Long id, @RequestBody UpdateResultatDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(resultatService.update(id, dto)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/valider")
    public ResponseEntity<SingleResultDto<ResultatDto>> valider(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(resultatService.valider(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/rejeter")
    public ResponseEntity<SingleResultDto<ResultatDto>> rejeter(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(resultatService.rejeter(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resultatService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
