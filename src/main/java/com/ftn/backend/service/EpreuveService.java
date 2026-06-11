package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.epreuve.CreateEpreuveDto;
import com.ftn.backend.dtos.epreuve.EpreuveDto;
import com.ftn.backend.dtos.epreuve.UpdateEpreuveDto;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Competition;
import com.ftn.backend.model.Epreuve;
import com.ftn.backend.repository.CompetitionRepository;
import com.ftn.backend.repository.EpreuveRepository;
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
public class EpreuveService {

    private final EpreuveRepository epreuveRepository;
    private final CompetitionRepository competitionRepository;

    @Transactional(readOnly = true)
    public EpreuveDto getById(Long id) {
        Epreuve epreuve = epreuveRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return toDto(epreuve);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<EpreuveDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<Epreuve> filters = new JpaQueryFilters<>(params, Epreuve.class);
        Page<Epreuve> page = epreuveRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<EpreuveDto> data = page.stream().map(this::toDto).toList();
        return ResponseEntity.ok(PageDto.<EpreuveDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional(readOnly = true)
    public List<EpreuveDto> getByCompetition(Long competitionId) {
        return epreuveRepository.findByCompetition_IdAndDeletedAtIsNull(competitionId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public EpreuveDto create(CreateEpreuveDto dto) {
        Competition competition = competitionRepository
                .findByIdAndDeletedAtIsNull(dto.getCompetitionId())
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found"));

        Epreuve epreuve = Epreuve.builder()
                .competition(competition)
                .swimStyle(dto.getSwimStyle())
                .distance(dto.getDistance())
                .gender(dto.getGender())
                .ageCategory(dto.getAgeCategory())
                .round(dto.getRound())
                .scheduledDate(dto.getScheduledDate())
                .build();

        return toDto(epreuveRepository.save(epreuve));
    }

    @Transactional
    public EpreuveDto update(Long id, UpdateEpreuveDto dto) {
        Epreuve epreuve = epreuveRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (dto.getSwimStyle() != null) epreuve.setSwimStyle(dto.getSwimStyle());
        if (dto.getDistance() != null) epreuve.setDistance(dto.getDistance());
        if (dto.getGender() != null) epreuve.setGender(dto.getGender());
        if (dto.getAgeCategory() != null) epreuve.setAgeCategory(dto.getAgeCategory());
        if (dto.getRound() != null) epreuve.setRound(dto.getRound());
        if (dto.getScheduledDate() != null) epreuve.setScheduledDate(dto.getScheduledDate());
        if (dto.getStatus() != null) epreuve.setStatus(dto.getStatus());

        return toDto(epreuveRepository.save(epreuve));
    }

    @Transactional
    public void delete(Long id) {
        Epreuve epreuve = epreuveRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        epreuve.setDeletedAt(LocalDateTime.now());
        epreuveRepository.save(epreuve);
    }

    public EpreuveDto toDto(Epreuve epreuve) {
        return EpreuveDto.builder()
                .id(epreuve.getId())
                .competitionId(epreuve.getCompetition().getId())
                .swimStyle(epreuve.getSwimStyle())
                .distance(epreuve.getDistance())
                .gender(epreuve.getGender())
                .ageCategory(epreuve.getAgeCategory())
                .round(epreuve.getRound())
                .scheduledDate(epreuve.getScheduledDate())
                .status(epreuve.getStatus())
                .createdAt(epreuve.getCreatedAt())
                .build();
    }
}
