package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.program.CreateProgramDto;
import com.ftn.backend.dtos.program.ProgramDto;
import com.ftn.backend.dtos.program.UpdateProgramDto;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Program;
import com.ftn.backend.repository.ProgramRepository;
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
public class ProgramService {

    private final ProgramRepository programRepository;

    @Transactional(readOnly = true)
    public ProgramDto getById(Long id) {
        Program program = programRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found"));
        return toDto(program);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<ProgramDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<Program> filters = new JpaQueryFilters<>(params, Program.class);
        Page<Program> page = programRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<ProgramDto> data = page.stream().map(this::toDto).toList();
        return ResponseEntity.ok(PageDto.<ProgramDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional(readOnly = true)
    public List<ProgramDto> getActives() {
        return programRepository.findByActifTrueAndDeletedAtIsNull().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ProgramDto create(CreateProgramDto dto) {
        Program program = Program.builder()
                .nom(dto.getNom())
                .description(dto.getDescription())
                .ageMin(dto.getAgeMin())
                .ageMax(dto.getAgeMax())
                .imageUrl(dto.getImageUrl())
                .actif(dto.getActif() != null ? dto.getActif() : true)
                .build();
        return toDto(programRepository.save(program));
    }

    @Transactional
    public ProgramDto update(Long id, UpdateProgramDto dto) {
        Program program = programRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found"));
        if (dto.getNom() != null) program.setNom(dto.getNom());
        if (dto.getDescription() != null) program.setDescription(dto.getDescription());
        if (dto.getAgeMin() != null) program.setAgeMin(dto.getAgeMin());
        if (dto.getAgeMax() != null) program.setAgeMax(dto.getAgeMax());
        if (dto.getImageUrl() != null) program.setImageUrl(dto.getImageUrl());
        if (dto.getActif() != null) program.setActif(dto.getActif());
        return toDto(programRepository.save(program));
    }

    @Transactional
    public void delete(Long id) {
        Program program = programRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found"));
        program.setDeletedAt(LocalDateTime.now());
        programRepository.save(program);
    }

    public ProgramDto toDto(Program program) {
        return ProgramDto.builder()
                .id(program.getId())
                .nom(program.getNom())
                .description(program.getDescription())
                .ageMin(program.getAgeMin())
                .ageMax(program.getAgeMax())
                .imageUrl(program.getImageUrl())
                .actif(program.getActif())
                .createdAt(program.getCreatedAt())
                .build();
    }
}
