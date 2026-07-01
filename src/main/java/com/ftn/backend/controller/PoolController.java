package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.pool.CreatePoolDto;
import com.ftn.backend.dtos.pool.PoolDto;
import com.ftn.backend.dtos.pool.UpdatePoolDto;
import com.ftn.backend.service.PoolService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pools")
@RequiredArgsConstructor
@Tag(name = "Pool", description = "Swimming pool management APIs")
public class PoolController {

    private final PoolService poolService;

    @GetMapping
    public ResponseEntity<PageDto<PoolDto>> getAll(@RequestParam Map<String, String> params) {
        return poolService.getAll(params);
    }

    @GetMapping("/actives")
    public ResponseEntity<List<PoolDto>> getActives() {
        return ResponseEntity.ok(poolService.getActives());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<PoolDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(poolService.getById(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SingleResultDto<PoolDto>> create(@Valid @RequestBody CreatePoolDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(poolService.create(dto)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<PoolDto>> update(@PathVariable Long id, @RequestBody UpdatePoolDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(poolService.update(id, dto)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        poolService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
