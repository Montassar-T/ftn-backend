package com.carServices.backend.controller;

import com.carServices.backend.dtos.*;
import com.carServices.backend.service.VehicleMakeService;
import io.swagger.v3.oas.annotations.enums.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vehicles/makes")
@Tag(name = "Vehicle Makes", description = "Vehicle Make APIs")
public class VehicleMakeController {

    private final VehicleMakeService vehicleMakeService;

    @GetMapping
    public ResponseEntity<PageDto<VehicleMakeDto>> getAllMakes(@RequestParam Map<String, String> params) {

        return ResponseEntity.ok(vehicleMakeService.getAllMakes(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<VehicleMakeDto>> getMakeById(@PathVariable Long id) {

        VehicleMakeDto result = vehicleMakeService.getMakeById(id);

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<VehicleMakeDto>> createMake(@RequestBody NewVehicleMakeDto dto) {

        VehicleMakeDto result = vehicleMakeService.createMake(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new SingleResultDto<>(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<VehicleMakeDto>> updateMake(
            @PathVariable Long id, @RequestBody NewVehicleMakeDto dto) {

        VehicleMakeDto result = vehicleMakeService.updateMake(id, dto);

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<InformativeMessage> deleteMake(@PathVariable Long id) {

        vehicleMakeService.deleteMake(id);

        return ResponseEntity.ok(new InformativeMessage("Vehicle Make deleted successfully"));
    }
}
