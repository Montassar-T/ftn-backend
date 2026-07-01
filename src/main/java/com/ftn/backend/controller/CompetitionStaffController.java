package com.ftn.backend.controller;

import com.ftn.backend.dtos.staff.CompetitionStaffDto;
import com.ftn.backend.dtos.staff.CreateCompetitionStaffDto;
import com.ftn.backend.service.CompetitionStaffService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Staff", description = "Competition staff management APIs")
public class CompetitionStaffController {

    private final CompetitionStaffService staffService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/v1/staff")
    public ResponseEntity<List<CompetitionStaffDto>> getAll() {
        return ResponseEntity.ok(staffService.getAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/v1/competitions/{id}/staff")
    public ResponseEntity<List<CompetitionStaffDto>> getByCompetition(@PathVariable Long id) {
        return ResponseEntity.ok(staffService.getByCompetition(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/v1/competitions/{id}/staff")
    public ResponseEntity<CompetitionStaffDto> addStaff(
            @PathVariable Long id, @RequestBody CreateCompetitionStaffDto dto) {
        return ResponseEntity.ok(staffService.addStaff(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/api/v1/staff/{staffId}")
    public ResponseEntity<Void> removeStaff(@PathVariable Long staffId) {
        staffService.removeStaff(staffId);
        return ResponseEntity.noContent().build();
    }
}
