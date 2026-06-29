package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.competition.CompetitionDto;
import com.ftn.backend.dtos.competition.CreateCompetitionDto;
import com.ftn.backend.dtos.competition.UpdateCompetitionDto;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Competition;
import com.ftn.backend.model.Pool;
import com.ftn.backend.model.User;
import com.ftn.backend.repository.CompetitionRepository;
import com.ftn.backend.repository.PoolRepository;
import com.ftn.backend.repository.UserRepository;
import com.ftn.backend.utils.JpaQueryFilters;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final PoolRepository poolRepository;
    private final UserRepository userRepository;

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

        User createdBy = null;
        if (dto.getCreatedById() != null) {
            createdBy = userRepository
                    .findByIdAndDeletedAtIsNull(dto.getCreatedById())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }

        Competition competition = Competition.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .type(dto.getType())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .registrationDeadline(dto.getRegistrationDeadline())
                .pool(pool)
                .lane(dto.getLane())
                .ageCategories(dto.getAgeCategories())
                .sourceUrl(dto.getSourceUrl())
                .createdBy(createdBy)
                .description(dto.getDescription())
                .build();
        return toDto(competitionRepository.save(competition));
    }

    @Transactional
    public CompetitionDto update(Long id, UpdateCompetitionDto dto) {
        Competition competition = competitionRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found"));

        if (dto.getCode() != null) competition.setCode(dto.getCode());
        if (dto.getName() != null) competition.setName(dto.getName());
        if (dto.getType() != null) competition.setType(dto.getType());
        if (dto.getStartDate() != null) competition.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) competition.setEndDate(dto.getEndDate());
        if (dto.getRegistrationDeadline() != null) competition.setRegistrationDeadline(dto.getRegistrationDeadline());
        if (dto.getStatus() != null) competition.setStatus(dto.getStatus());
        if (dto.getDescription() != null) competition.setDescription(dto.getDescription());
        if (dto.getNbParticipants() != null) competition.setNbParticipants(dto.getNbParticipants());
        if (dto.getLane() != null) competition.setLane(dto.getLane());
        if (dto.getAgeCategories() != null) competition.setAgeCategories(dto.getAgeCategories());
        if (dto.getSourceUrl() != null) competition.setSourceUrl(dto.getSourceUrl());
        if (dto.getPoolId() != null) {
            Pool pool = poolRepository
                    .findByIdAndDeletedAtIsNull(dto.getPoolId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pool not found"));
            competition.setPool(pool);
        }
        if (dto.getCreatedById() != null) {
            User createdBy = userRepository
                    .findByIdAndDeletedAtIsNull(dto.getCreatedById())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            competition.setCreatedBy(createdBy);
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
        competition.setStatus("EN_COURS");
        return toDto(competitionRepository.save(competition));
    }

    @Transactional
    public CompetitionDto terminer(Long id) {
        Competition competition = competitionRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found"));
        competition.setStatus("TERMINEE");
        return toDto(competitionRepository.save(competition));
    }

    /**
     * Returns competitions whose date range overlaps the given interval.
     * Used for conflict detection when creating or editing a competition.
     */
    public List<CompetitionDto> findConflicts(LocalDate startDate, LocalDate endDate, Long excludeId) {
        return competitionRepository.findConflicting(startDate, endDate, excludeId).stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Called hourly by the scheduler to auto-transition statuses based on today's date.
     *   PLANIFIEE → EN_COURS  when today >= startDate (and today <= endDate)
     *   EN_COURS  → TERMINEE  when today > endDate
     */
    @Transactional
    public void updateStatusesForDate(LocalDate today) {
        // PLANIFIEE → EN_COURS
        competitionRepository.findByStatusAndDeletedAtIsNull("PLANIFIEE").stream()
                .filter(c -> c.getStartDate() != null && !today.isBefore(c.getStartDate()))
                .filter(c -> c.getEndDate() != null && !today.isAfter(c.getEndDate()))
                .forEach(c -> {
                    c.setStatus("EN_COURS");
                    competitionRepository.save(c);
                    log.info("Competition [{}] '{}' → EN_COURS", c.getId(), c.getName());
                });

        // EN_COURS → TERMINEE
        competitionRepository.findByStatusAndDeletedAtIsNull("EN_COURS").stream()
                .filter(c -> c.getEndDate() != null && today.isAfter(c.getEndDate()))
                .forEach(c -> {
                    c.setStatus("TERMINEE");
                    competitionRepository.save(c);
                    log.info("Competition [{}] '{}' → TERMINEE", c.getId(), c.getName());
                });
    }

    public CompetitionDto toDto(Competition competition) {
        return CompetitionDto.builder()
                .id(competition.getId())
                .code(competition.getCode())
                .name(competition.getName())
                .type(competition.getType())
                .startDate(competition.getStartDate())
                .endDate(competition.getEndDate())
                .registrationDeadline(competition.getRegistrationDeadline())
                .poolId(competition.getPool() != null ? competition.getPool().getId() : null)
                .poolNom(competition.getPool() != null ? competition.getPool().getNom() : null)
                .lane(competition.getLane())
                .ageCategories(competition.getAgeCategories())
                .sourceUrl(competition.getSourceUrl())
                .createdById(
                        competition.getCreatedBy() != null
                                ? competition.getCreatedBy().getId()
                                : null)
                .status(competition.getStatus())
                .description(competition.getDescription())
                .nbParticipants(competition.getNbParticipants())
                .createdAt(competition.getCreatedAt())
                .build();
    }
}
