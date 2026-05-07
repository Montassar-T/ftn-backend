package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.reponse.CreateReponseDto;
import com.ftn.backend.dtos.reponse.ReponseDto;
import com.ftn.backend.dtos.reponse.UpdateReponseDto;
import com.ftn.backend.service.ReponseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reponses")
@RequiredArgsConstructor
@Tag(name = "Reponse", description = "Forum reply management APIs")
public class ReponseController {

    private final ReponseService reponseService;

    @GetMapping
    public ResponseEntity<PageDto<ReponseDto>> getAll(@RequestParam Map<String, String> params) {
        return reponseService.getAll(params);
    }

    @GetMapping("/sujet/{sujetId}")
    public ResponseEntity<List<ReponseDto>> getBySujet(@PathVariable Long sujetId) {
        return ResponseEntity.ok(reponseService.getBySujet(sujetId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<ReponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(reponseService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<ReponseDto>> create(
            @RequestBody CreateReponseDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(reponseService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<ReponseDto>> update(
            @PathVariable Long id, @RequestBody UpdateReponseDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(reponseService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reponseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/liker")
    public ResponseEntity<SingleResultDto<ReponseDto>> liker(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(reponseService.liker(id)));
    }

    @PutMapping("/{id}/signaler")
    public ResponseEntity<SingleResultDto<ReponseDto>> signaler(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(reponseService.signaler(id)));
    }
}
