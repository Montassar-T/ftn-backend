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
                .orElseThrow(() -> new ResourceNotFoundException("Epreuve not found"));
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
                .nom(dto.getNom())
                .discipline(dto.getDiscipline())
                .categorie(dto.getCategorie())
                .sexe(dto.getSexe())
                .distance(dto.getDistance())
                .style(dto.getStyle())
                .dateHeure(dto.getDateHeure())
                .build();

        return toDto(epreuveRepository.save(epreuve));
    }

    @Transactional
    public EpreuveDto update(Long id, UpdateEpreuveDto dto) {
        Epreuve epreuve = epreuveRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Epreuve not found"));

        if (dto.getNom() != null) epreuve.setNom(dto.getNom());
        if (dto.getDiscipline() != null) epreuve.setDiscipline(dto.getDiscipline());
        if (dto.getCategorie() != null) epreuve.setCategorie(dto.getCategorie());
        if (dto.getSexe() != null) epreuve.setSexe(dto.getSexe());
        if (dto.getDistance() != null) epreuve.setDistance(dto.getDistance());
        if (dto.getStyle() != null) epreuve.setStyle(dto.getStyle());
        if (dto.getDateHeure() != null) epreuve.setDateHeure(dto.getDateHeure());

        return toDto(epreuveRepository.save(epreuve));
    }

    @Transactional
    public void delete(Long id) {
        Epreuve epreuve = epreuveRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Epreuve not found"));
        epreuve.setDeletedAt(LocalDateTime.now());
        epreuveRepository.save(epreuve);
    }

    public EpreuveDto toDto(Epreuve epreuve) {
        return EpreuveDto.builder()
                .id(epreuve.getId())
                .competitionId(epreuve.getCompetition().getId())
                .nom(epreuve.getNom())
                .discipline(epreuve.getDiscipline())
                .categorie(epreuve.getCategorie())
                .sexe(epreuve.getSexe())
                .distance(epreuve.getDistance())
                .style(epreuve.getStyle())
                .dateHeure(epreuve.getDateHeure())
                .createdAt(epreuve.getCreatedAt())
                .build();
    }
}
