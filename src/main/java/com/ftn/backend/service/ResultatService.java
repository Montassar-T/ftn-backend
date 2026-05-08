package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.resultat.CreateResultatDto;
import com.ftn.backend.dtos.resultat.ResultatDto;
import com.ftn.backend.dtos.resultat.UpdateResultatDto;
import com.ftn.backend.exception.business.ConflictException;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Athlete;
import com.ftn.backend.model.Epreuve;
import com.ftn.backend.model.Resultat;
import com.ftn.backend.repository.AthleteRepository;
import com.ftn.backend.repository.EpreuveRepository;
import com.ftn.backend.repository.ResultatRepository;
import com.ftn.backend.utils.JpaQueryFilters;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResultatService {

    private final ResultatRepository resultatRepository;
    private final AthleteRepository athleteRepository;
    private final EpreuveRepository epreuveRepository;
    private final ClassementService classementService;

    @Transactional(readOnly = true)
    public ResultatDto getById(Long id) {
        Resultat resultat = resultatRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultat not found"));
        return toDto(resultat);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<ResultatDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<Resultat> filters = new JpaQueryFilters<>(params, Resultat.class);
        Page<Resultat> page = resultatRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<ResultatDto> data = page.stream().map(this::toDto).toList();
        return ResponseEntity.ok(PageDto.<ResultatDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional(readOnly = true)
    public List<ResultatDto> getByAthlete(Long athleteId) {
        return resultatRepository.findByAthlete_IdAndDeletedAtIsNull(athleteId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ResultatDto create(CreateResultatDto dto) {
        Athlete athlete = athleteRepository
                .findByIdAndDeletedAtIsNull(dto.getAthleteId())
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found"));

        Epreuve epreuve = epreuveRepository
                .findByIdAndDeletedAtIsNull(dto.getEpreuveId())
                .orElseThrow(() -> new ResourceNotFoundException("Epreuve not found"));

        if (resultatRepository.existsByEpreuve_IdAndAthlete_IdAndDeletedAtIsNull(
                dto.getEpreuveId(), dto.getAthleteId())) {
            throw new ConflictException("Result already exists for this athlete and epreuve");
        }

        Resultat resultat = Resultat.builder()
                .athlete(athlete)
                .epreuve(epreuve)
                .temps(dto.getTemps())
                .classement(dto.getClassement())
                .record(dto.getRecord() != null ? dto.getRecord() : false)
                .points(dto.getPoints() != null ? dto.getPoints() : 0)
                .dateSaisie(LocalDateTime.now())
                .build();

        Resultat saved = resultatRepository.save(resultat);
        classementService.rebuild(
                epreuve.getDiscipline(),
                epreuve.getCategorie(),
                epreuve.getSexe(),
                epreuve.getDateHeure().getYear());

        return toDto(saved);
    }

    @Transactional
    public ResultatDto update(Long id, UpdateResultatDto dto) {
        Resultat resultat = resultatRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultat not found"));

        if (dto.getTemps() != null) resultat.setTemps(dto.getTemps());
        if (dto.getClassement() != null) resultat.setClassement(dto.getClassement());
        if (dto.getRecord() != null) resultat.setRecord(dto.getRecord());
        if (dto.getPoints() != null) resultat.setPoints(dto.getPoints());

        Resultat saved = resultatRepository.save(resultat);
        Epreuve epreuve = resultat.getEpreuve();
        classementService.rebuild(
                epreuve.getDiscipline(),
                epreuve.getCategorie(),
                epreuve.getSexe(),
                epreuve.getDateHeure().getYear());

        return toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        Resultat resultat = resultatRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultat not found"));
        resultat.setDeletedAt(LocalDateTime.now());
        resultatRepository.save(resultat);

        Epreuve epreuve = resultat.getEpreuve();
        classementService.rebuild(
                epreuve.getDiscipline(),
                epreuve.getCategorie(),
                epreuve.getSexe(),
                epreuve.getDateHeure().getYear());
    }

    @Transactional
    public ResultatDto publier(Long id) {
        Resultat resultat = resultatRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resultat not found"));
        resultat.setPublie(true);
        return toDto(resultatRepository.save(resultat));
    }

    public ResultatDto toDto(Resultat resultat) {
        return ResultatDto.builder()
                .id(resultat.getId())
                .epreuveId(resultat.getEpreuve().getId())
                .athleteId(resultat.getAthlete().getId())
                .temps(resultat.getTemps())
                .classement(resultat.getClassement())
                .record(resultat.getRecord())
                .points(resultat.getPoints())
                .dateSaisie(resultat.getDateSaisie())
                .publie(resultat.getPublie())
                .createdAt(resultat.getCreatedAt())
                .build();
    }
}
