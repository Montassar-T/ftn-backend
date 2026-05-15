package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.competition.CompetitionDto;
import com.ftn.backend.dtos.competition.CreateCompetitionDto;
import com.ftn.backend.dtos.competition.UpdateCompetitionDto;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Competition;
import com.ftn.backend.model.User;
import com.ftn.backend.repository.CompetitionRepository;
import com.ftn.backend.repository.UserRepository;
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
        User createdBy = null;
        if (dto.getCreatedById() != null) {
            createdBy = userRepository
                    .findByIdAndDeletedAtIsNull(dto.getCreatedById())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }

        Competition competition = Competition.builder()
                .name(dto.getName())
                .type(dto.getType())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .registrationDeadline(dto.getRegistrationDeadline())
                .poolId(dto.getPoolId())
                .createdBy(createdBy)
                .build();
        return toDto(competitionRepository.save(competition));
    }

    @Transactional
    public CompetitionDto update(Long id, UpdateCompetitionDto dto) {
        Competition competition = competitionRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found"));

        if (dto.getName() != null) competition.setName(dto.getName());
        if (dto.getType() != null) competition.setType(dto.getType());
        if (dto.getStartDate() != null) competition.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) competition.setEndDate(dto.getEndDate());
        if (dto.getRegistrationDeadline() != null) competition.setRegistrationDeadline(dto.getRegistrationDeadline());
        if (dto.getPoolId() != null) competition.setPoolId(dto.getPoolId());
        if (dto.getStatus() != null) competition.setStatus(dto.getStatus());
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

    public CompetitionDto toDto(Competition competition) {
        return CompetitionDto.builder()
                .id(competition.getId())
                .name(competition.getName())
                .type(competition.getType())
                .startDate(competition.getStartDate())
                .endDate(competition.getEndDate())
                .registrationDeadline(competition.getRegistrationDeadline())
                .poolId(competition.getPoolId())
                .createdById(
                        competition.getCreatedBy() != null
                                ? competition.getCreatedBy().getId()
                                : null)
                .status(competition.getStatus())
                .createdAt(competition.getCreatedAt())
                .build();
    }
}
