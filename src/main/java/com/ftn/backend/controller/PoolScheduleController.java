package com.ftn.backend.controller;

import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.schedule.CreatePoolScheduleDto;
import com.ftn.backend.dtos.schedule.PoolScheduleDto;
import com.ftn.backend.service.PoolScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "PoolSchedules", description = "Pool schedule management APIs")
public class PoolScheduleController {

    private final PoolScheduleService scheduleService;

    @GetMapping("/pools/{poolId}/schedules")
    public ResponseEntity<SingleResultDto<List<PoolScheduleDto>>> getByPool(@PathVariable Long poolId) {
        return ResponseEntity.ok(new SingleResultDto<>(scheduleService.getByPool(poolId)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/pool-schedules")
    public ResponseEntity<SingleResultDto<PoolScheduleDto>> create(@Valid @RequestBody CreatePoolScheduleDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(scheduleService.create(dto)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/pool-schedules/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
