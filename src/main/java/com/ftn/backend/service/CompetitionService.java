package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.competition.CompetitionDto;
import com.ftn.backend.dtos.competition.CreateCompetitionDto;
import com.ftn.backend.dtos.competition.UpdateCompetitionDto;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Competition;
import com.ftn.backend.repository.CompetitionRepository;
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
        Competition competition = Competition.builder()
                .nom(dto.getNom())
                .discipline(dto.getDiscipline())
                .dateDebut(dto.getDateDebut())
                .dateFin(dto.getDateFin())
                .lieu(dto.getLieu())
                .region(dto.getRegion())
                .niveau(dto.getNiveau())
                .build();
        return toDto(competitionRepository.save(competition));
    }

    @Transactional
    public CompetitionDto update(Long id, UpdateCompetitionDto dto) {
        Competition competition = competitionRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found"));

        if (dto.getNom() != null) competition.setNom(dto.getNom());
        if (dto.getDiscipline() != null) competition.setDiscipline(dto.getDiscipline());
        if (dto.getDateDebut() != null) competition.setDateDebut(dto.getDateDebut());
        if (dto.getDateFin() != null) competition.setDateFin(dto.getDateFin());
        if (dto.getLieu() != null) competition.setLieu(dto.getLieu());
        if (dto.getRegion() != null) competition.setRegion(dto.getRegion());
        if (dto.getNiveau() != null) competition.setNiveau(dto.getNiveau());
        if (dto.getStatut() != null) competition.setStatut(dto.getStatut());

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
                .nom(competition.getNom())
                .discipline(competition.getDiscipline())
                .dateDebut(competition.getDateDebut())
                .dateFin(competition.getDateFin())
                .lieu(competition.getLieu())
                .region(competition.getRegion())
                .niveau(competition.getNiveau())
                .statut(competition.getStatut())
                .createdAt(competition.getCreatedAt())
                .build();
    }
}
