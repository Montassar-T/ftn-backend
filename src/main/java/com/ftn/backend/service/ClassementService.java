package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.classement.ClassementDto;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Classement;
import com.ftn.backend.model.Epreuve;
import com.ftn.backend.model.Resultat;
import com.ftn.backend.repository.ClassementRepository;
import com.ftn.backend.repository.EpreuveRepository;
import com.ftn.backend.repository.ResultatRepository;
import com.ftn.backend.utils.JpaQueryFilters;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClassementService {

    private final ClassementRepository classementRepository;
    private final ResultatRepository resultatRepository;
    private final EpreuveRepository epreuveRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<ClassementDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<Classement> filters = new JpaQueryFilters<>(params, Classement.class);
        Page<Classement> page = classementRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<ClassementDto> data = page.stream().map(this::toDto).toList();
        return ResponseEntity.ok(PageDto.<ClassementDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional(readOnly = true)
    public List<ClassementDto> getByAthlete(Long athleteId) {
        return classementRepository.findByAthlete_IdAndDeletedAtIsNull(athleteId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClassementDto> getNationalRanking(Long eventId, String season) {
        return classementRepository.findByEpreuve_IdAndSeasonAndDeletedAtIsNullOrderByRankAsc(eventId, season).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public List<ClassementDto> rebuild(Long eventId, String season) {
        Epreuve epreuve = epreuveRepository
                .findByIdAndDeletedAtIsNull(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        List<Resultat> matchingResults = new ArrayList<>();
        for (Resultat r : resultatRepository.findByEpreuve_IdAndDeletedAtIsNull(eventId)) {
            if (r.getTempsMs() == null) continue;
            String resultSeason =
                    String.valueOf(r.getEpreuve().getScheduledDate().getYear());
            if (!season.equals(resultSeason)) continue;
            matchingResults.add(r);
        }

        Map<Long, Integer> bestTimeByAthlete = new HashMap<>();
        Map<Long, Resultat> sourceByAthlete = new HashMap<>();
        for (Resultat r : matchingResults) {
            Long athleteId = r.getAthlete().getId();
            int time = r.getTempsMs();
            if (!bestTimeByAthlete.containsKey(athleteId) || time < bestTimeByAthlete.get(athleteId)) {
                bestTimeByAthlete.put(athleteId, time);
                sourceByAthlete.put(athleteId, r);
            }
        }

        List<Map.Entry<Long, Integer>> ordered = bestTimeByAthlete.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                .toList();

        List<Classement> oldRows =
                classementRepository.findByEpreuve_IdAndSeasonAndDeletedAtIsNullOrderByRankAsc(eventId, season);
        LocalDateTime now = LocalDateTime.now();
        oldRows.forEach(row -> row.setDeletedAt(now));
        classementRepository.saveAll(oldRows);

        List<Classement> refreshed = new ArrayList<>();
        int rank = 1;
        for (Map.Entry<Long, Integer> entry : ordered) {
            Resultat source = sourceByAthlete.get(entry.getKey());
            Classement classement = Classement.builder()
                    .athlete(source.getAthlete())
                    .epreuve(epreuve)
                    .bestTimeMs(entry.getValue())
                    .bestTimeDisplay(source.getTempsDisplay())
                    .pointsFina(source.getPointsFina())
                    .rank(rank++)
                    .season(season)
                    .build();
            refreshed.add(classement);
        }

        List<Classement> saved = classementRepository.saveAll(refreshed);
        return saved.stream().map(this::toDto).toList();
    }

    public ClassementDto toDto(Classement classement) {
        Epreuve epreuve = classement.getEpreuve();
        com.ftn.backend.model.Athlete athlete = classement.getAthlete();

        String athleteName = null;
        String athleteNationality = null;
        Long clubId = null;
        String clubName = null;
        if (athlete != null) {
            if (athlete.getUser() != null) {
                String fn = athlete.getUser().getFirstName();
                String ln = athlete.getUser().getLastName();
                athleteName = ((fn != null ? fn : "") + " " + (ln != null ? ln : "")).trim();
            }
            athleteNationality = athlete.getNationalite();
            if (athlete.getClub() != null) {
                clubId = athlete.getClub().getId();
                clubName = athlete.getClub().getNom();
            }
        }

        return ClassementDto.builder()
                .id(classement.getId())
                .athleteId(athlete != null ? athlete.getId() : null)
                .athleteName(athleteName)
                .athleteNationality(athleteNationality)
                .clubId(clubId)
                .clubName(clubName)
                .eventId(epreuve.getId())
                .swimStyle(epreuve.getSwimStyle())
                .distance(epreuve.getDistance())
                .gender(epreuve.getGender())
                .ageCategory(epreuve.getAgeCategory())
                .bestTimeMs(classement.getBestTimeMs())
                .bestTimeDisplay(classement.getBestTimeDisplay())
                .pointsFina(classement.getPointsFina())
                .rank(classement.getRank())
                .season(classement.getSeason())
                .createdAt(classement.getCreatedAt())
                .build();
    }
}
