package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.reponse.CreateReponseDto;
import com.ftn.backend.dtos.reponse.ReponseDto;
import com.ftn.backend.dtos.reponse.UpdateReponseDto;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Reponse;
import com.ftn.backend.model.Sujet;
import com.ftn.backend.repository.ReponseRepository;
import com.ftn.backend.repository.SujetRepository;
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
public class ReponseService {

    private final ReponseRepository reponseRepository;
    private final SujetRepository sujetRepository;

    @Transactional(readOnly = true)
    public ReponseDto getById(Long id) {
        Reponse reponse = reponseRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reponse not found"));
        return toDto(reponse);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<ReponseDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<Reponse> filters = new JpaQueryFilters<>(params, Reponse.class);
        Page<Reponse> page =
                reponseRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<ReponseDto> data = page.stream().map(this::toDto).toList();
        return ResponseEntity.ok(PageDto.<ReponseDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional(readOnly = true)
    public List<ReponseDto> getBySujet(Long sujetId) {
        return reponseRepository.findBySujet_IdAndDeletedAtIsNull(sujetId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ReponseDto create(CreateReponseDto dto) {
        Sujet sujet = sujetRepository
                .findByIdAndDeletedAtIsNull(dto.getSujetId())
                .orElseThrow(() -> new ResourceNotFoundException("Sujet not found"));

        Reponse reponse = Reponse.builder()
                .sujet(sujet)
                .contenu(dto.getContenu())
                .dateCreation(LocalDateTime.now())
                .build();

        Reponse saved = reponseRepository.save(reponse);
        sujet.setNbReponses(sujet.getNbReponses() + 1);
        sujetRepository.save(sujet);
        return toDto(saved);
    }

    @Transactional
    public ReponseDto update(Long id, UpdateReponseDto dto) {
        Reponse reponse = reponseRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reponse not found"));
        if (dto.getContenu() != null) reponse.setContenu(dto.getContenu());
        return toDto(reponseRepository.save(reponse));
    }

    @Transactional
    public void delete(Long id) {
        Reponse reponse = reponseRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reponse not found"));
        reponse.setDeletedAt(LocalDateTime.now());
        reponseRepository.save(reponse);
    }

    @Transactional
    public ReponseDto liker(Long id) {
        Reponse reponse = reponseRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reponse not found"));
        reponse.setNbLikes(reponse.getNbLikes() + 1);
        return toDto(reponseRepository.save(reponse));
    }

    @Transactional
    public ReponseDto signaler(Long id) {
        Reponse reponse = reponseRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reponse not found"));
        reponse.setSignale(true);
        return toDto(reponseRepository.save(reponse));
    }

    public ReponseDto toDto(Reponse reponse) {
        return ReponseDto.builder()
                .id(reponse.getId())
                .sujetId(reponse.getSujet().getId())
                .auteurId(reponse.getAuteur() != null ? reponse.getAuteur().getId() : null)
                .contenu(reponse.getContenu())
                .dateCreation(reponse.getDateCreation())
                .nbLikes(reponse.getNbLikes())
                .signale(reponse.getSignale())
                .createdAt(reponse.getCreatedAt())
                .build();
    }
}
