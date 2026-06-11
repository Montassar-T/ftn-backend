package com.ftn.backend.controller;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.SingleResultDto;
import com.ftn.backend.dtos.athlete.AthleteDto;
import com.ftn.backend.dtos.club.ClubDto;
import com.ftn.backend.dtos.club.CreateClubDto;
import com.ftn.backend.dtos.club.UpdateClubDto;
import com.ftn.backend.service.ClubService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clubs")
@RequiredArgsConstructor
@Tag(name = "Club", description = "Club management APIs")
public class ClubController {

    private final ClubService clubService;

    @GetMapping
    public ResponseEntity<PageDto<ClubDto>> getAll(@RequestParam Map<String, String> params) {
        return clubService.getAll(params);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResultDto<ClubDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new SingleResultDto<>(clubService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<SingleResultDto<ClubDto>> create(@Valid @RequestBody CreateClubDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(clubService.create(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SingleResultDto<ClubDto>> update(@PathVariable Long id, @RequestBody UpdateClubDto dto) {
        return ResponseEntity.ok(new SingleResultDto<>(clubService.update(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clubService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/athletes")
    public ResponseEntity<List<AthleteDto>> getAthletes(@PathVariable Long id) {
        return ResponseEntity.ok(clubService.getAthletes(id));
    }
}
