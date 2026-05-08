package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/resultats")
@RequiredArgsConstructor
@Tag(name = "Resultat", description = "Results management APIs")
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

    @PostMapping
    public ResponseEntity<SingleResultDto<ResultatDto>> create(@Valid @RequestBody CreateResultatDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(resultatService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<ResultatDto>> update(
            @PathVariable Long id, @RequestBody UpdateResultatDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(resultatService.update(id, dto)));
    }

    @PutMapping("/{id}/publier")
    public ResponseEntity<SingleResultDto<ResultatDto>> publier(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(resultatService.publier(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resultatService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
