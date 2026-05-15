package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.classement.ClassementDto;
import com.ftn.backend.model.Classement;
import com.ftn.backend.model.Resultat;
import com.ftn.backend.repository.ClassementRepository;
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
    public List<ClassementDto> getNationalRanking(String swimStyle, String distance, String season) {
        return classementRepository
                .findBySwimStyleAndDistanceAndSeasonAndDeletedAtIsNullOrderByRankAsc(swimStyle, distance, season)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public List<ClassementDto> rebuild(String swimStyle, String distance, String season) {
        List<Resultat> matchingResults = new ArrayList<>();
        for (Resultat r : resultatRepository.findAll()) {
            if (r.getDeletedAt() != null) continue;
            Resultat resultat = r;
            if (!swimStyle.equals(resultat.getEpreuve().getSwimStyle())) continue;
            if (!distance.equals(resultat.getEpreuve().getDistance())) continue;
            String resultSeason =
                    String.valueOf(resultat.getEpreuve().getScheduledDate().getYear());
            if (!season.equals(resultSeason)) continue;
            if (resultat.getFinalTime() == null) continue;
            matchingResults.add(resultat);
        }

        Map<Long, Integer> bestTimeByAthlete = new HashMap<>();
        Map<Long, Resultat> sourceByAthlete = new HashMap<>();
        for (Resultat r : matchingResults) {
            Long athleteId = r.getAthlete().getId();
            int time = r.getFinalTime();
            if (!bestTimeByAthlete.containsKey(athleteId) || time < bestTimeByAthlete.get(athleteId)) {
                bestTimeByAthlete.put(athleteId, time);
                sourceByAthlete.put(athleteId, r);
            }
        }

        List<Map.Entry<Long, Integer>> ordered = bestTimeByAthlete.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                .toList();

        List<Classement> oldRows =
                classementRepository.findBySwimStyleAndDistanceAndSeasonAndDeletedAtIsNullOrderByRankAsc(
                        swimStyle, distance, season);
        LocalDateTime now = LocalDateTime.now();
        oldRows.forEach(row -> row.setDeletedAt(now));
        classementRepository.saveAll(oldRows);

        List<Classement> refreshed = new ArrayList<>();
        int rank = 1;
        for (Map.Entry<Long, Integer> entry : ordered) {
            Resultat source = sourceByAthlete.get(entry.getKey());
            Classement classement = Classement.builder()
                    .athlete(source.getAthlete())
                    .swimStyle(swimStyle)
                    .distance(distance)
                    .bestTime(entry.getValue())
                    .rank(rank++)
                    .season(season)
                    .build();
            refreshed.add(classement);
        }

        List<Classement> saved = classementRepository.saveAll(refreshed);
        return saved.stream().map(this::toDto).toList();
    }

    public ClassementDto toDto(Classement classement) {
        return ClassementDto.builder()
                .id(classement.getId())
                .athleteId(classement.getAthlete().getId())
                .swimStyle(classement.getSwimStyle())
                .distance(classement.getDistance())
                .bestTime(classement.getBestTime())
                .rank(classement.getRank())
                .season(classement.getSeason())
                .createdAt(classement.getCreatedAt())
                .build();
    }
}
