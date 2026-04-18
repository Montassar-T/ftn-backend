package com.carServices.backend.controller;

import com.carServices.backend.dtos.*;
import com.carServices.backend.service.VehicleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vehicles")
@Tag(name = "Vehicle", description = "Vehicle APIs")
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<SingleResultDto<PageDto<VehicleDto>>> getVehicles(@RequestParam Map<String, String> params) {

        PageDto<VehicleDto> result = vehicleService.getVehicles(params).getBody();

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<VehicleDto>> getVehicleById(@PathVariable Long id) {

        VehicleDto result = vehicleService.getVehicleById(id).getBody();

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<VehicleDto>> createVehicle(@RequestBody NewVehicleDto vehicleDto) {

        VehicleDto result = vehicleService.createVehicle(vehicleDto).getBody();

        return ResponseEntity.status(HttpStatus.CREATED).body(new SingleResultDto<>(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<VehicleDto>> updateVehicle(
            @PathVariable Long id, @RequestBody NewVehicleDto vehicleDto) {

        VehicleDto result = vehicleService.updateVehicle(id, vehicleDto).getBody();

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SingleResultDto<InformativeMessage>> deleteVehicle(@PathVariable Long id) {

        InformativeMessage result = vehicleService.deleteVehicle(id).getBody();

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }
}
