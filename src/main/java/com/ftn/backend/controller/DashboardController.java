package com.ftn.backend.controller;

import com.ftn.backend.dtos.DashboardStatsDto;
import com.ftn.backend.dtos.competition.CompetitionDto;
import com.ftn.backend.enums.CompetitionStatutEnum;
import com.ftn.backend.model.Competition;
import com.ftn.backend.repository.ActualiteRepository;
import com.ftn.backend.repository.AthleteRepository;
import com.ftn.backend.repository.ClubRepository;
import com.ftn.backend.repository.CompetitionRepository;
import com.ftn.backend.repository.CompetitionStaffRepository;
import com.ftn.backend.repository.LicenceRepository;
import com.ftn.backend.repository.PoolRepository;
import com.ftn.backend.repository.ResultRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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
    private final LicenceRepository licenceRepository;
    private final ResultRepository resultRepository;
    private final ActualiteRepository actualiteRepository;
    private final CompetitionStaffRepository staffRepository;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getStats() {

        List<Competition> recent = competitionRepository.findTop5ByDeletedAtIsNullOrderByCreatedAtDesc();

        List<CompetitionDto> recentDtos = recent.stream()
                .map(c -> CompetitionDto.builder()
                        .id(c.getId())
                        .nom(c.getNom())
                        .type(c.getType())
                        .dateDebut(c.getDateDebut())
                        .dateFin(c.getDateFin())
                        .poolId(c.getPool() != null ? c.getPool().getId() : null)
                        .poolNom(c.getPool() != null ? c.getPool().getNom() : null)
                        .statut(c.getStatut())
                        .nbParticipants(c.getNbParticipants())
                        .createdAt(c.getCreatedAt())
                        .build())
                .toList();

        DashboardStatsDto stats = DashboardStatsDto.builder()
                .nbAthletes(athleteRepository.countByDeletedAtIsNull())
                .nbCompetitions(competitionRepository.countByDeletedAtIsNull())
                .nbClubs(clubRepository.countByDeletedAtIsNull())
                .nbPiscines(poolRepository.countByDeletedAtIsNull())
                .nbLicences(licenceRepository.countByDeletedAtIsNull())
                .nbResults(resultRepository.countByDeletedAtIsNull())
                .nbActualites(actualiteRepository.countByDeletedAtIsNull())
                .nbStaff(staffRepository.countByDeletedAtIsNull())
                .nbActiveCompetitions(
                        competitionRepository.countByStatutAndDeletedAtIsNull(CompetitionStatutEnum.EN_COURS))
                .recentCompetitions(recentDtos)
                .build();

        return ResponseEntity.ok(stats);
    }
}
