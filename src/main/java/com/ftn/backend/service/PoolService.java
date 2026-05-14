package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.pool.CreatePoolDto;
import com.ftn.backend.dtos.pool.PoolDto;
import com.ftn.backend.dtos.pool.UpdatePoolDto;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Pool;
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
public class PoolService {

    private final PoolRepository poolRepository;

    @Transactional(readOnly = true)
    public PoolDto getById(Long id) {
        Pool pool = poolRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pool not found"));
        return toDto(pool);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<PoolDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<Pool> filters = new JpaQueryFilters<>(params, Pool.class);
        Page<Pool> page = poolRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<PoolDto> data = page.stream().map(this::toDto).toList();
        return ResponseEntity.ok(PageDto.<PoolDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional(readOnly = true)
    public List<PoolDto> getActives() {
        return poolRepository.findByActifTrueAndDeletedAtIsNull().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public PoolDto create(CreatePoolDto dto) {
        Pool pool = Pool.builder()
                .nom(dto.getNom())
                .ville(dto.getVille())
                .adresse(dto.getAdresse())
                .longueur(dto.getLongueur())
                .nbCouloirs(dto.getNbCouloirs())
                .type(dto.getType())
                .actif(dto.getActif() != null ? dto.getActif() : true)
                .build();
        return toDto(poolRepository.save(pool));
    }

    @Transactional
    public PoolDto update(Long id, UpdatePoolDto dto) {
        Pool pool = poolRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pool not found"));
        if (dto.getNom() != null) pool.setNom(dto.getNom());
        if (dto.getVille() != null) pool.setVille(dto.getVille());
        if (dto.getAdresse() != null) pool.setAdresse(dto.getAdresse());
        if (dto.getLongueur() != null) pool.setLongueur(dto.getLongueur());
        if (dto.getNbCouloirs() != null) pool.setNbCouloirs(dto.getNbCouloirs());
        if (dto.getType() != null) pool.setType(dto.getType());
        if (dto.getActif() != null) pool.setActif(dto.getActif());
        return toDto(poolRepository.save(pool));
    }

    @Transactional
    public void delete(Long id) {
        Pool pool = poolRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pool not found"));
        pool.setDeletedAt(LocalDateTime.now());
        poolRepository.save(pool);
    }

    public PoolDto toDto(Pool pool) {
        return PoolDto.builder()
                .id(pool.getId())
                .nom(pool.getNom())
                .ville(pool.getVille())
                .adresse(pool.getAdresse())
                .longueur(pool.getLongueur())
                .nbCouloirs(pool.getNbCouloirs())
                .type(pool.getType())
                .actif(pool.getActif())
                .createdAt(pool.getCreatedAt())
                .build();
    }
}
