package com.carServices.backend.controller;

import com.carServices.backend.dtos.*;
import com.carServices.backend.service.MechanicService;
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
@RequestMapping("/api/v1/mechanics")
@Tag(name = "Mechanic", description = "Mechanic APIs")
public class MechanicController {

    private final MechanicService mechanicService;

    @GetMapping("")
    public ResponseEntity<PageDto<MechanicDto>> getMechanics(
            @Parameter(
                            name = "params",
                            in = ParameterIn.QUERY,
                            schema = @Schema(implementation = MechanicDto.class),
                            style = ParameterStyle.FORM,
                            explode = Explode.TRUE)
                    @RequestParam
                    Map<String, String> params) {
        return mechanicService.getMechanics(params);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MechanicDto> getMechanicById(@PathVariable Long id) {
        return mechanicService.getMechanicById(id);
    }

    @PostMapping
    public ResponseEntity<MechanicDto> createMechanic(@RequestBody NewMechanicDto dto) {
        return mechanicService.createMechanic(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MechanicDto> updateMechanic(@PathVariable Long id, @RequestBody NewMechanicDto dto) {
        return mechanicService.updateMechanic(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<InformativeMessage> deleteMechanic(@PathVariable Long id) {
        return mechanicService.deleteMechanic(id);
    }
}
