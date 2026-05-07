package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.actualite.ActualiteDto;
import com.ftn.backend.dtos.actualite.CreateActualiteDto;
import com.ftn.backend.dtos.actualite.UpdateActualiteDto;
import com.ftn.backend.enums.CategorieActuEnum;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Actualite;
import com.ftn.backend.model.User;
import com.ftn.backend.repository.ActualiteRepository;
import com.ftn.backend.repository.UserRepository;
import com.ftn.backend.utils.JpaQueryFilters;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActualiteService {

    private final ActualiteRepository actualiteRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ActualiteDto getById(Long id) {
        Actualite actualite = actualiteRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actualite not found"));
        return toDto(actualite);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<ActualiteDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<Actualite> filters = new JpaQueryFilters<>(params, Actualite.class);
        Page<Actualite> page =
                actualiteRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<ActualiteDto> data = page.stream().map(this::toDto).toList();
        return ResponseEntity.ok(PageDto.<ActualiteDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional(readOnly = true)
    public List<ActualiteDto> getPubliees() {
        return actualiteRepository.findByPublieTrueAndDeletedAtIsNull().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ActualiteDto> getByCategorie(CategorieActuEnum categorie) {
        return actualiteRepository.findByCategorieAndDeletedAtIsNull(categorie).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ActualiteDto> search(String titre) {
        return actualiteRepository
                .findByTitreContainingIgnoreCaseAndDeletedAtIsNull(titre)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ActualiteDto create(CreateActualiteDto dto) {
        User auteur = getCurrentUser();
        Actualite actualite = Actualite.builder()
                .auteur(auteur)
                .titre(dto.getTitre())
                .contenu(dto.getContenu())
                .categorie(dto.getCategorie())
                .imageUrl(dto.getImageUrl())
                .build();
        return toDto(actualiteRepository.save(actualite));
    }

    @Transactional
    public ActualiteDto update(Long id, UpdateActualiteDto dto) {
        Actualite actualite = actualiteRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actualite not found"));
        if (dto.getTitre() != null) actualite.setTitre(dto.getTitre());
        if (dto.getContenu() != null) actualite.setContenu(dto.getContenu());
        if (dto.getCategorie() != null) actualite.setCategorie(dto.getCategorie());
        if (dto.getImageUrl() != null) actualite.setImageUrl(dto.getImageUrl());
        return toDto(actualiteRepository.save(actualite));
    }

    @Transactional
    public void delete(Long id) {
        Actualite actualite = actualiteRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actualite not found"));
        actualite.setDeletedAt(LocalDateTime.now());
        actualiteRepository.save(actualite);
    }

    @Transactional
    public ActualiteDto publier(Long id) {
        Actualite actualite = actualiteRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actualite not found"));
        actualite.setPublie(true);
        actualite.setDatePublication(LocalDateTime.now());
        return toDto(actualiteRepository.save(actualite));
    }

    @Transactional
    public ActualiteDto archiver(Long id) {
        Actualite actualite = actualiteRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actualite not found"));
        actualite.setPublie(false);
        return toDto(actualiteRepository.save(actualite));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmailAndDeletedAtIsNull(email).orElse(null);
    }

    public ActualiteDto toDto(Actualite actualite) {
        return ActualiteDto.builder()
                .id(actualite.getId())
                .auteurId(actualite.getAuteur() != null ? actualite.getAuteur().getId() : null)
                .titre(actualite.getTitre())
                .contenu(actualite.getContenu())
                .categorie(actualite.getCategorie())
                .datePublication(actualite.getDatePublication())
                .publie(actualite.getPublie())
                .imageUrl(actualite.getImageUrl())
                .createdAt(actualite.getCreatedAt())
                .build();
    }
}
