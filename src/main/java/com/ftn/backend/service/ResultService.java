package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.result.CreateResultDto;
import com.ftn.backend.dtos.result.ResultDto;
import com.ftn.backend.dtos.result.UpdateResultDto;
import com.ftn.backend.enums.ResultStatutEnum;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Athlete;
import com.ftn.backend.model.Competition;
import com.ftn.backend.model.Result;
import com.ftn.backend.repository.AthleteRepository;
import com.ftn.backend.repository.CompetitionRepository;
import com.ftn.backend.repository.ResultRepository;
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
public class ResultService {

    private final ResultRepository resultRepository;
    private final AthleteRepository athleteRepository;
    private final CompetitionRepository competitionRepository;

    @Transactional(readOnly = true)
    public ResultDto getById(Long id) {
        Result result = resultRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found"));
        return toDto(result);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<ResultDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<Result> filters = new JpaQueryFilters<>(params, Result.class);
        Page<Result> page = resultRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<ResultDto> data = page.stream().map(this::toDto).toList();
        return ResponseEntity.ok(PageDto.<ResultDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional(readOnly = true)
    public List<ResultDto> getByAthlete(Long athleteId) {
        return resultRepository.findByAthlete_IdAndDeletedAtIsNull(athleteId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResultDto> getByCompetition(Long competitionId) {
        return resultRepository.findByCompetition_IdAndDeletedAtIsNull(competitionId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ResultDto create(CreateResultDto dto) {
        Athlete athlete = athleteRepository
                .findByIdAndDeletedAtIsNull(dto.getAthleteId())
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found"));
        Competition competition = competitionRepository
                .findByIdAndDeletedAtIsNull(dto.getCompetitionId())
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found"));

        Result result = Result.builder()
                .athlete(athlete)
                .competition(competition)
                .epreuve(dto.getEpreuve())
                .temps(dto.getTemps())
                .rang(dto.getRang())
                .build();
        return toDto(resultRepository.save(result));
    }

    @Transactional
    public ResultDto update(Long id, UpdateResultDto dto) {
        Result result = resultRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found"));
        if (dto.getEpreuve() != null) result.setEpreuve(dto.getEpreuve());
        if (dto.getTemps() != null) result.setTemps(dto.getTemps());
        if (dto.getRang() != null) result.setRang(dto.getRang());
        if (dto.getStatut() != null) result.setStatut(dto.getStatut());
        if (dto.getAthleteId() != null) {
            Athlete athlete = athleteRepository
                    .findByIdAndDeletedAtIsNull(dto.getAthleteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Athlete not found"));
            result.setAthlete(athlete);
        }
        if (dto.getCompetitionId() != null) {
            Competition competition = competitionRepository
                    .findByIdAndDeletedAtIsNull(dto.getCompetitionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Competition not found"));
            result.setCompetition(competition);
        }
        return toDto(resultRepository.save(result));
    }

    @Transactional
    public void delete(Long id) {
        Result result = resultRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found"));
        result.setDeletedAt(LocalDateTime.now());
        resultRepository.save(result);
    }

    @Transactional
    public ResultDto valider(Long id) {
        Result result = resultRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found"));
        result.setStatut(ResultStatutEnum.VALIDE);
        return toDto(resultRepository.save(result));
    }

    @Transactional
    public ResultDto rejeter(Long id) {
        Result result = resultRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found"));
        result.setStatut(ResultStatutEnum.REJETE);
        return toDto(resultRepository.save(result));
    }

    public ResultDto toDto(Result result) {
        String athleteNom = null;
        if (result.getAthlete() != null && result.getAthlete().getUser() != null) {
            athleteNom = result.getAthlete().getUser().getFirstName() + " "
                    + result.getAthlete().getUser().getLastName();
        }
        return ResultDto.builder()
                .id(result.getId())
                .athleteId(result.getAthlete() != null ? result.getAthlete().getId() : null)
                .athleteNom(athleteNom)
                .competitionId(
                        result.getCompetition() != null
                                ? result.getCompetition().getId()
                                : null)
                .competitionNom(
                        result.getCompetition() != null
                                ? result.getCompetition().getNom()
                                : null)
                .epreuve(result.getEpreuve())
                .temps(result.getTemps())
                .rang(result.getRang())
                .statut(result.getStatut())
                .createdAt(result.getCreatedAt())
                .build();
    }
}
