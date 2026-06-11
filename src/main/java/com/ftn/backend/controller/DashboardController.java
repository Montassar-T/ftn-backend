package com.ftn.backend.controller;

import com.ftn.backend.dtos.DashboardStatsDto;
import com.ftn.backend.repository.AthleteRepository;
import com.ftn.backend.repository.ClubRepository;
import com.ftn.backend.repository.CompetitionRepository;
import com.ftn.backend.repository.PoolRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard statistics APIs")
public class DashboardController {

    private final AthleteRepository athleteRepository;
    private final CompetitionRepository competitionRepository;
    private final ClubRepository clubRepository;
    private final PoolRepository poolRepository;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getStats() {
        DashboardStatsDto stats = DashboardStatsDto.builder()
                .nbAthletes(athleteRepository.countByDeletedAtIsNull())
                .nbCompetitions(competitionRepository.countByDeletedAtIsNull())
                .nbClubs(clubRepository.countByDeletedAtIsNull())
                .nbPiscines(poolRepository.countByDeletedAtIsNull())
                .build();
        return ResponseEntity.ok(stats);
    }
}
