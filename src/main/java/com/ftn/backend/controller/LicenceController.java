package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.licence.CreateLicenceDto;
import com.ftn.backend.dtos.licence.LicenceDto;
import com.ftn.backend.dtos.licence.UpdateLicenceDto;
import com.ftn.backend.service.LicenceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/licences")
@RequiredArgsConstructor
@Tag(name = "Licence", description = "Licence management APIs")
public class LicenceController {

    private final LicenceService licenceService;

    @GetMapping
    public ResponseEntity<PageDto<LicenceDto>> getAll(@RequestParam Map<String, String> params) {
        return licenceService.getAll(params);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<LicenceDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(licenceService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<LicenceDto>> create(@Valid @RequestBody CreateLicenceDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(licenceService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<LicenceDto>> update(
            @PathVariable Long id, @RequestBody UpdateLicenceDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(licenceService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        licenceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/valider")
    public ResponseEntity<SingleResultDto<LicenceDto>> valider(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(licenceService.valider(id)));
    }

    @PutMapping("/{id}/rejeter")
    public ResponseEntity<SingleResultDto<LicenceDto>> rejeter(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(licenceService.rejeter(id)));
    }
}
