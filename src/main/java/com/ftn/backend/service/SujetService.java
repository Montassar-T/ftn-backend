package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.sujet.CreateSujetDto;
import com.ftn.backend.dtos.sujet.SujetDto;
import com.ftn.backend.dtos.sujet.UpdateSujetDto;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Forum;
import com.ftn.backend.model.Sujet;
import com.ftn.backend.model.User;
import com.ftn.backend.repository.ForumRepository;
import com.ftn.backend.repository.ReponseRepository;
import com.ftn.backend.repository.SujetRepository;
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
public class SujetService {

    private final SujetRepository sujetRepository;
    private final ForumRepository forumRepository;
    private final ReponseRepository reponseRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public SujetDto getById(Long id) {
        Sujet sujet = sujetRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sujet not found"));
        return toDto(sujet);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<SujetDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<Sujet> filters = new JpaQueryFilters<>(params, Sujet.class);
        Page<Sujet> page = sujetRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<SujetDto> data = page.stream().map(this::toDto).toList();
        return ResponseEntity.ok(PageDto.<SujetDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional(readOnly = true)
    public List<SujetDto> getByForum(Long forumId) {
        return sujetRepository.findByForum_IdAndDeletedAtIsNull(forumId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public SujetDto create(CreateSujetDto dto) {
        Forum forum = forumRepository
                .findByIdAndDeletedAtIsNull(dto.getForumId())
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found"));

        Sujet sujet = Sujet.builder()
                .forum(forum)
                .auteur(getCurrentUser())
                .titre(dto.getTitre())
                .contenu(dto.getContenu())
                .dateCreation(LocalDateTime.now())
                .build();

        Sujet saved = sujetRepository.save(sujet);
        forum.setNbSujets(forum.getNbSujets() + 1);
        forumRepository.save(forum);
        return toDto(saved);
    }

    @Transactional
    public SujetDto update(Long id, UpdateSujetDto dto) {
        Sujet sujet = sujetRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sujet not found"));
        if (dto.getTitre() != null) sujet.setTitre(dto.getTitre());
        if (dto.getContenu() != null) sujet.setContenu(dto.getContenu());
        return toDto(sujetRepository.save(sujet));
    }

    @Transactional
    public void delete(Long id) {
        Sujet sujet = sujetRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sujet not found"));

        LocalDateTime now = LocalDateTime.now();

        reponseRepository.findBySujet_IdAndDeletedAtIsNull(id).forEach(reponse -> {
            reponse.setDeletedAt(now);
            reponseRepository.save(reponse);
        });

        sujet.setDeletedAt(now);
        sujetRepository.save(sujet);
    }

    @Transactional
    public SujetDto epingler(Long id) {
        Sujet sujet = sujetRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sujet not found"));
        sujet.setEpingle(!sujet.getEpingle());
        return toDto(sujetRepository.save(sujet));
    }

    @Transactional
    public SujetDto fermer(Long id) {
        Sujet sujet = sujetRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sujet not found"));
        sujet.setFerme(true);
        return toDto(sujetRepository.save(sujet));
    }

    @Transactional
    public SujetDto incrementerVues(Long id) {
        Sujet sujet = sujetRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sujet not found"));
        sujet.setNbVues(sujet.getNbVues() + 1);
        return toDto(sujetRepository.save(sujet));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmailAndDeletedAtIsNull(email).orElse(null);
    }

    public SujetDto toDto(Sujet sujet) {
        return SujetDto.builder()
                .id(sujet.getId())
                .forumId(sujet.getForum().getId())
                .auteurId(sujet.getAuteur() != null ? sujet.getAuteur().getId() : null)
                .titre(sujet.getTitre())
                .contenu(sujet.getContenu())
                .dateCreation(sujet.getDateCreation())
                .epingle(sujet.getEpingle())
                .ferme(sujet.getFerme())
                .nbVues(sujet.getNbVues())
                .nbReponses(sujet.getNbReponses())
                .createdAt(sujet.getCreatedAt())
                .build();
    }
}
