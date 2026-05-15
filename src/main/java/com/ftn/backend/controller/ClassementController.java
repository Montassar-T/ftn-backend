package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.classement.ClassementDto;
import com.ftn.backend.dtos.classement.RebuildClassementDto;
import com.ftn.backend.service.ClassementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rankings")
@RequiredArgsConstructor
@Tag(name = "NationalRanking", description = "National ranking management APIs")
public class ClassementController {

    private final ClassementService classementService;

    @GetMapping
    public ResponseEntity<PageDto<ClassementDto>> getAll(@RequestParam Map<String, String> params) {
        return classementService.getAll(params);
    }

    @GetMapping("/athlete/{athleteId}")
    public ResponseEntity<List<ClassementDto>> getByAthlete(@PathVariable Long athleteId) {
        return ResponseEntity.ok(classementService.getByAthlete(athleteId));
    }

    @GetMapping("/national")
    public ResponseEntity<List<ClassementDto>> getNational(
            @RequestParam String swimStyle, @RequestParam String distance, @RequestParam String season) {
        return ResponseEntity.ok(classementService.getNationalRanking(swimStyle, distance, season));
    }

    @PostMapping("/rebuild")
    public ResponseEntity<SingleResultDto<List<ClassementDto>>> rebuild(@Valid @RequestBody RebuildClassementDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(
                classementService.rebuild(dto.getSwimStyle(), dto.getDistance(), dto.getSeason())));
    }
}
