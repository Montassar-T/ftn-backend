package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.equipe.CreateEquipeNationaleDto;
import com.ftn.backend.dtos.equipe.EquipeNationaleDto;
import com.ftn.backend.dtos.equipe.UpdateEquipeNationaleDto;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Athlete;
import com.ftn.backend.model.EquipeNationale;
import com.ftn.backend.repository.AthleteRepository;
import com.ftn.backend.repository.EquipeNationaleRepository;
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
public class EquipeNationaleService {

    private final EquipeNationaleRepository equipeRepository;
    private final AthleteRepository athleteRepository;

    @Transactional(readOnly = true)
    public EquipeNationaleDto getById(Long id) {
        EquipeNationale equipe = equipeRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipe nationale not found"));
        return toDto(equipe);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<EquipeNationaleDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<EquipeNationale> filters = new JpaQueryFilters<>(params, EquipeNationale.class);
        Page<EquipeNationale> page = equipeRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<EquipeNationaleDto> data = page.stream().map(this::toDto).toList();
        return ResponseEntity.ok(PageDto.<EquipeNationaleDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional
    public EquipeNationaleDto create(CreateEquipeNationaleDto dto) {
        EquipeNationale equipe = EquipeNationale.builder()
                .discipline(dto.getDiscipline())
                .categorie(dto.getCategorie())
                .annee(dto.getAnnee())
                .build();
        return toDto(equipeRepository.save(equipe));
    }

    @Transactional
    public EquipeNationaleDto update(Long id, UpdateEquipeNationaleDto dto) {
        EquipeNationale equipe = equipeRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipe nationale not found"));
        if (dto.getDiscipline() != null) equipe.setDiscipline(dto.getDiscipline());
        if (dto.getCategorie() != null) equipe.setCategorie(dto.getCategorie());
        if (dto.getAnnee() != null) equipe.setAnnee(dto.getAnnee());
        return toDto(equipeRepository.save(equipe));
    }

    @Transactional
    public void delete(Long id) {
        EquipeNationale equipe = equipeRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipe nationale not found"));
        equipe.setDeletedAt(LocalDateTime.now());
        equipeRepository.save(equipe);
    }

    @Transactional
    public EquipeNationaleDto addMembre(Long equipeId, Long athleteId) {
        EquipeNationale equipe = equipeRepository
                .findByIdAndDeletedAtIsNull(equipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipe nationale not found"));
        Athlete athlete = athleteRepository
                .findByIdAndDeletedAtIsNull(athleteId)
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found"));
        equipe.getMembres().add(athlete);
        return toDto(equipeRepository.save(equipe));
    }

    @Transactional
    public EquipeNationaleDto removeMembre(Long equipeId, Long athleteId) {
        EquipeNationale equipe = equipeRepository
                .findByIdAndDeletedAtIsNull(equipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipe nationale not found"));
        equipe.getMembres().removeIf(a -> a.getId().equals(athleteId));
        return toDto(equipeRepository.save(equipe));
    }

    public EquipeNationaleDto toDto(EquipeNationale equipe) {
        List<Long> membresIds = equipe.getMembres().stream().map(Athlete::getId).toList();
        return EquipeNationaleDto.builder()
                .id(equipe.getId())
                .discipline(equipe.getDiscipline())
                .categorie(equipe.getCategorie())
                .annee(equipe.getAnnee())
                .membresIds(membresIds)
                .createdAt(equipe.getCreatedAt())
                .build();
    }
}
