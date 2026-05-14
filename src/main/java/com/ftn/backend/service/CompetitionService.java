package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.competition.CompetitionDto;
import com.ftn.backend.dtos.competition.CreateCompetitionDto;
import com.ftn.backend.dtos.competition.UpdateCompetitionDto;
import com.ftn.backend.enums.CompetitionStatutEnum;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Competition;
import com.ftn.backend.model.Pool;
import com.ftn.backend.repository.CompetitionRepository;
import com.ftn.backend.repository.PoolRepository;
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
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final PoolRepository poolRepository;

    @Transactional(readOnly = true)
    public CompetitionDto getById(Long id) {
        Competition competition = competitionRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found"));
        return toDto(competition);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<CompetitionDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<Competition> filters = new JpaQueryFilters<>(params, Competition.class);
        Page<Competition> page = competitionRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<CompetitionDto> data = page.stream().map(this::toDto).toList();
        return ResponseEntity.ok(PageDto.<CompetitionDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional
    public CompetitionDto create(CreateCompetitionDto dto) {
        Pool pool = null;
        if (dto.getPoolId() != null) {
            pool = poolRepository
                    .findByIdAndDeletedAtIsNull(dto.getPoolId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pool not found"));
        }

        Competition competition = Competition.builder()
                .nom(dto.getNom())
                .type(dto.getType())
                .dateDebut(dto.getDateDebut())
                .dateFin(dto.getDateFin())
                .pool(pool)
                .description(dto.getDescription())
                .build();
        return toDto(competitionRepository.save(competition));
    }

    @Transactional
    public CompetitionDto update(Long id, UpdateCompetitionDto dto) {
        Competition competition = competitionRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found"));
        if (dto.getNom() != null) competition.setNom(dto.getNom());
        if (dto.getType() != null) competition.setType(dto.getType());
        if (dto.getDateDebut() != null) competition.setDateDebut(dto.getDateDebut());
        if (dto.getDateFin() != null) competition.setDateFin(dto.getDateFin());
        if (dto.getStatut() != null) competition.setStatut(dto.getStatut());
        if (dto.getDescription() != null) competition.setDescription(dto.getDescription());
        if (dto.getNbParticipants() != null) competition.setNbParticipants(dto.getNbParticipants());
        if (dto.getPoolId() != null) {
            Pool pool = poolRepository
                    .findByIdAndDeletedAtIsNull(dto.getPoolId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pool not found"));
            competition.setPool(pool);
        }
        return toDto(competitionRepository.save(competition));
    }

    @Transactional
    public void delete(Long id) {
        Competition competition = competitionRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found"));
        competition.setDeletedAt(LocalDateTime.now());
        competitionRepository.save(competition);
    }

    @Transactional
    public CompetitionDto demarrer(Long id) {
        Competition competition = competitionRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found"));
        competition.setStatut(CompetitionStatutEnum.EN_COURS);
        return toDto(competitionRepository.save(competition));
    }

    @Transactional
    public CompetitionDto terminer(Long id) {
        Competition competition = competitionRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found"));
        competition.setStatut(CompetitionStatutEnum.TERMINE);
        return toDto(competitionRepository.save(competition));
    }

    public CompetitionDto toDto(Competition competition) {
        return CompetitionDto.builder()
                .id(competition.getId())
                .nom(competition.getNom())
                .type(competition.getType())
                .dateDebut(competition.getDateDebut())
                .dateFin(competition.getDateFin())
                .poolId(competition.getPool() != null ? competition.getPool().getId() : null)
                .poolNom(competition.getPool() != null ? competition.getPool().getNom() : null)
                .statut(competition.getStatut())
                .description(competition.getDescription())
                .nbParticipants(competition.getNbParticipants())
                .createdAt(competition.getCreatedAt())
                .build();
    }
}
