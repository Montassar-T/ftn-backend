package com.carServices.backend.controller;

import com.carServices.backend.dtos.InformativeMessage;
import com.carServices.backend.dtos.NewVehicleDto;
import com.carServices.backend.dtos.PageDto;
import com.carServices.backend.dtos.VehicleDto;
import com.carServices.backend.service.VehicleService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vehicles")
@Tag(name = "Vehicle", description = "Vehicle APIs")
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping("")
    public ResponseEntity<PageDto<VehicleDto>> getVehicles(
            @Parameter(
                            name = "params",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "object", implementation = VehicleDto.class),
                            style = ParameterStyle.FORM,
                            explode = Explode.TRUE)
                    @RequestParam
                    Map<String, String> params) {
        return vehicleService.getVehicles(params);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDto> getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id);
    }

    @PostMapping
    public ResponseEntity<VehicleDto> createVehicle(@RequestBody NewVehicleDto vehicleDto) {
        return vehicleService.createVehicle(vehicleDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleDto> updateVehicle(@PathVariable Long id, @RequestBody NewVehicleDto vehicleDto) {
        return vehicleService.updateVehicle(id, vehicleDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<InformativeMessage> deleteVehicle(@PathVariable Long id) {
        return vehicleService.deleteVehicle(id);
    }
}
