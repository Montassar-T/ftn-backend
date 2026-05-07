package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.actualite.ActualiteDto;
import com.ftn.backend.dtos.actualite.CreateActualiteDto;
import com.ftn.backend.dtos.actualite.UpdateActualiteDto;
import com.ftn.backend.enums.CategorieActuEnum;
import com.ftn.backend.service.ActualiteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/actualites")
@RequiredArgsConstructor
@Tag(name = "Actualite", description = "News and content management APIs")
public class ActualiteController {

    private final ActualiteService actualiteService;

    @GetMapping
    public ResponseEntity<PageDto<ActualiteDto>> getAll(
            @RequestParam Map<String, String> params) {
        return actualiteService.getAll(params);
    }

    @GetMapping("/publiees")
    public ResponseEntity<List<ActualiteDto>> getPubliees() {
        return ResponseEntity.ok(actualiteService.getPubliees());
    }

    @GetMapping("/categorie/{categorie}")
    public ResponseEntity<List<ActualiteDto>> getByCategorie(
            @PathVariable CategorieActuEnum categorie) {
        return ResponseEntity.ok(actualiteService.getByCategorie(categorie));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ActualiteDto>> search(@RequestParam String titre) {
        return ResponseEntity.ok(actualiteService.search(titre));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<ActualiteDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(actualiteService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<ActualiteDto>> create(
            @Valid @RequestBody CreateActualiteDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(actualiteService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<ActualiteDto>> update(
            @PathVariable Long id, @RequestBody UpdateActualiteDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(actualiteService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        actualiteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/publier")
    public ResponseEntity<SingleResultDto<ActualiteDto>> publier(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(actualiteService.publier(id)));
    }

    @PutMapping("/{id}/archiver")
    public ResponseEntity<SingleResultDto<ActualiteDto>> archiver(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(actualiteService.archiver(id)));
    }
}
