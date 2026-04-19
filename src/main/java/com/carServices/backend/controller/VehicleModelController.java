package com.carServices.backend.controller;

import com.carServices.backend.dtos.*;
import com.carServices.backend.service.VehicleModelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vehicles/models")
@Tag(name = "Vehicle Models", description = "Vehicle Model APIs")
public class VehicleModelController {

    private final VehicleModelService vehicleModelService;

    @GetMapping
    public ResponseEntity<PageDto<VehicleModelDto>> getAllModels(@RequestParam Map<String, String> params) {

        return ResponseEntity.ok(vehicleModelService.getAllModels(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<VehicleModelDto>> getModelById(@PathVariable Long id) {

        VehicleModelDto result = vehicleModelService.getModelById(id);

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }

    @GetMapping("/by-make/{makeId}")
    public ResponseEntity<PageDto<VehicleModelLiteDto>> getModelsByMake(@PathVariable Long makeId) {

        List<VehicleModelLiteDto> modelsList = vehicleModelService.getModelsByMake(makeId);

        return ResponseEntity.ok(PageDto.<VehicleModelLiteDto>builder()
                .data(modelsList)
                .total(modelsList.size())
                .build());
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<VehicleModelDto>> createModel(@RequestBody NewVehicleModelDto dto) {

        VehicleModelDto result = vehicleModelService.createModel(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new SingleResultDto<>(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<VehicleModelDto>> updateModel(
            @PathVariable Long id, @RequestBody NewVehicleModelDto dto) {

        VehicleModelDto result = vehicleModelService.updateModel(id, dto);

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<InformativeMessage> deleteModel(@PathVariable Long id) {

        vehicleModelService.deleteModel(id);

        return ResponseEntity.ok(new InformativeMessage("Vehicle model deleted successfully"));
    }
}
