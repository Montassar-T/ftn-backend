package com.carServices.backend.controller;

import com.carServices.backend.dtos.*;
import com.carServices.backend.service.MechanicService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mechanics")
@Tag(name = "Mechanic", description = "Mechanic APIs")
public class MechanicController {

    private final MechanicService mechanicService;

    @GetMapping
    public ResponseEntity<SingleResultDto<PageDto<MechanicDto>>> getMechanics(
            @RequestParam Map<String, String> params) {

        PageDto<MechanicDto> result = mechanicService.getMechanics(params).getBody();

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<MechanicDto>> getMechanicById(@PathVariable Long id) {

        MechanicDto result = mechanicService.getMechanicById(id).getBody();

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<MechanicDto>> createMechanic(@RequestBody NewMechanicDto dto) {

        MechanicDto result = mechanicService.createMechanic(dto).getBody();

        return ResponseEntity.status(HttpStatus.CREATED).body(new SingleResultDto<>(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<MechanicDto>> updateMechanic(
            @PathVariable Long id, @RequestBody NewMechanicDto dto) {

        MechanicDto result = mechanicService.updateMechanic(id, dto).getBody();

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SingleResultDto<InformativeMessage>> deleteMechanic(@PathVariable Long id) {

        InformativeMessage result = mechanicService.deleteMechanic(id).getBody();

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }
}
