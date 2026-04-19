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
    public ResponseEntity<PageDto<VehicleDto>> getVehicles(@RequestParam Map<String, String> params) {

        return ResponseEntity.ok(vehicleService.getVehicles(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<VehicleDto>> getVehicleById(@PathVariable Long id) {

        VehicleDto result = vehicleService.getVehicleById(id);

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<VehicleDto>> createVehicle(@RequestBody NewVehicleDto vehicleDto) {

        VehicleDto result = vehicleService.createVehicle(vehicleDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new SingleResultDto<>(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<VehicleDto>> updateVehicle(
            @PathVariable Long id, @RequestBody NewVehicleDto vehicleDto) {

        VehicleDto result = vehicleService.updateVehicle(id, vehicleDto);

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<InformativeMessage> deleteVehicle(@PathVariable Long id) {

        vehicleService.deleteVehicle(id);

        return ResponseEntity.ok(new InformativeMessage("Vehicle deleted successfully"));
    }
}
