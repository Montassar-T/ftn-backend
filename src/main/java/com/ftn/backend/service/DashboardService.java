package com.ftn.backend.service;

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
import com.ftn.backend.repository.ResultatRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AthleteRepository athleteRepository;
    private final CompetitionRepository competitionRepository;
    private final ClubRepository clubRepository;
    private final PoolRepository poolRepository;
    private final LicenceRepository licenceRepository;
    private final ResultatRepository resultatRepository;
    private final ActualiteRepository actualiteRepository;
    private final CompetitionStaffRepository staffRepository;

    @Transactional(readOnly = true)
    public DashboardStatsDto getStats() {
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

        return DashboardStatsDto.builder()
                .nbAthletes(athleteRepository.countByDeletedAtIsNull())
                .nbCompetitions(competitionRepository.countByDeletedAtIsNull())
                .nbClubs(clubRepository.countByDeletedAtIsNull())
                .nbPiscines(poolRepository.countByDeletedAtIsNull())
                .nbLicences(licenceRepository.countByDeletedAtIsNull())
                .nbResults(resultatRepository.countByDeletedAtIsNull())
                .nbActualites(actualiteRepository.countByDeletedAtIsNull())
                .nbStaff(staffRepository.countByDeletedAtIsNull())
                .nbActiveCompetitions(
                        competitionRepository.countByStatutAndDeletedAtIsNull(CompetitionStatutEnum.EN_COURS))
                .recentCompetitions(recentDtos)
                .build();
    }
}
