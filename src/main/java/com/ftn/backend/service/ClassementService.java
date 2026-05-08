package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.classement.ClassementDto;
import com.ftn.backend.enums.CategorieEnum;
import com.ftn.backend.enums.DisciplineEnum;
import com.ftn.backend.enums.SexeEnum;
import com.ftn.backend.model.Classement;
import com.ftn.backend.model.Resultat;
import com.ftn.backend.repository.ClassementRepository;
import com.ftn.backend.repository.ResultatRepository;
import com.ftn.backend.utils.JpaQueryFilters;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
    public List<ClassementDto> getClassement(
            DisciplineEnum discipline, CategorieEnum categorie, SexeEnum sexe, Integer annee) {
        return classementRepository
                .findByDisciplineAndCategorieAndSexeAndAnneeAndDeletedAtIsNullOrderByRangAsc(
                        discipline, categorie, sexe, annee)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public List<ClassementDto> rebuild(
            DisciplineEnum discipline, CategorieEnum categorie, SexeEnum sexe, Integer annee) {

        List<Resultat> results = new ArrayList<>();
        for (Resultat resultat : resultatRepository.findAll()) {
            if (resultat.getDeletedAt() != null || !Boolean.TRUE.equals(resultat.getPublie())) {
                continue;
            }
            if (resultat.getEpreuve().getDiscipline() != discipline) continue;
            if (resultat.getEpreuve().getCategorie() != categorie) continue;
            if (resultat.getEpreuve().getSexe() != sexe) continue;
            if (resultat.getEpreuve().getDateHeure().getYear() != annee) continue;
            results.add(resultat);
        }

        Map<Long, Integer> pointsByAthlete = new java.util.HashMap<>();
        for (Resultat resultat : results) {
            pointsByAthlete.merge(resultat.getAthlete().getId(), resultat.getPoints(), Integer::sum);
        }

        List<Map.Entry<Long, Integer>> ordered = pointsByAthlete.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue(Comparator.reverseOrder()))
                .toList();

        List<Classement> oldRows =
                classementRepository.findByDisciplineAndCategorieAndSexeAndAnneeAndDeletedAtIsNullOrderByRangAsc(
                        discipline, categorie, sexe, annee);
        LocalDateTime now = LocalDateTime.now();
        oldRows.forEach(row -> row.setDeletedAt(now));
        classementRepository.saveAll(oldRows);

        List<Classement> refreshed = new ArrayList<>();
        int rang = 1;
        for (Map.Entry<Long, Integer> entry : ordered) {
            Resultat source = results.stream()
                    .filter(r -> r.getAthlete().getId().equals(entry.getKey()))
                    .findFirst()
                    .orElse(null);
            if (source == null) {
                continue;
            }

            Classement classement = Classement.builder()
                    .athlete(source.getAthlete())
                    .discipline(discipline)
                    .categorie(categorie)
                    .sexe(sexe)
                    .annee(annee)
                    .pointsTotal(entry.getValue())
                    .rang(rang++)
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
                .discipline(classement.getDiscipline())
                .categorie(classement.getCategorie())
                .sexe(classement.getSexe())
                .annee(classement.getAnnee())
                .pointsTotal(classement.getPointsTotal())
                .rang(classement.getRang())
                .createdAt(classement.getCreatedAt())
                .build();
    }
}
