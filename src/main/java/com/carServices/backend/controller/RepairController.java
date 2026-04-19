package com.carServices.backend.controller;

import com.carServices.backend.dtos.*;
import com.carServices.backend.service.RepairService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/repairs")
@RequiredArgsConstructor
@Tag(name = "Repair", description = "Repair APIs")
public class RepairController {
    private final RepairService repairService;

    @GetMapping
    public ResponseEntity<PageDto<RepairDto>> getRepairs(@RequestParam Map<String, String> params) {

        return ResponseEntity.ok(repairService.getRepairs(params));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<RepairDto>> getRepairById(@PathVariable Long id) {

        RepairDto result = repairService.getRepairById(id);

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<RepairDto>> createRepair(@RequestBody NewRepairDto dto) {

        RepairDto result = repairService.createRepair(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new SingleResultDto<>(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<RepairDto>> updateRepair(
            @PathVariable Long id, @RequestBody NewRepairDto dto) {

        RepairDto result = repairService.updateRepair(id, dto);

        return ResponseEntity.ok(new SingleResultDto<>(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<InformativeMessage> deleteRepair(@PathVariable Long id) {

        repairService.deleteRepair(id);

        return ResponseEntity.ok(new InformativeMessage("Repair deleted successfully"));
    }
}
