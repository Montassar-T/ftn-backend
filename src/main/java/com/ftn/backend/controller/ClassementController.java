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
@RequestMapping("/api/v1/classements")
@RequiredArgsConstructor
@Tag(name = "Classement", description = "Ranking management APIs")
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
            @RequestParam com.ftn.backend.enums.DisciplineEnum discipline,
            @RequestParam com.ftn.backend.enums.CategorieEnum categorie,
            @RequestParam com.ftn.backend.enums.SexeEnum sexe,
            @RequestParam Integer annee) {
        return ResponseEntity.ok(classementService.getClassement(discipline, categorie, sexe, annee));
    }

    @PostMapping("/rebuild")
    public ResponseEntity<SingleResultDto<List<ClassementDto>>> rebuild(@Valid @RequestBody RebuildClassementDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(
                classementService.rebuild(dto.getDiscipline(), dto.getCategorie(), dto.getSexe(), dto.getAnnee())));
    }
}
