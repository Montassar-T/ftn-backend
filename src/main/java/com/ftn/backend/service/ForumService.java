package com.ftn.backend.service;

import com.ftn.backend.dtos.forum.CreateForumDto;
import com.ftn.backend.dtos.forum.ForumDto;
import com.ftn.backend.dtos.forum.UpdateForumDto;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Forum;
import com.ftn.backend.repository.ForumRepository;
import com.ftn.backend.repository.ReponseRepository;
import com.ftn.backend.repository.SujetRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ForumService {

    private final ForumRepository forumRepository;
    private final SujetRepository sujetRepository;
    private final ReponseRepository reponseRepository;

    @Transactional(readOnly = true)
    public ForumDto getById(Long id) {
        Forum forum = forumRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found"));
        return toDto(forum);
    }

    @Transactional(readOnly = true)
    public List<ForumDto> getAll() {
        return forumRepository.findAllByDeletedAtIsNull().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ForumDto create(CreateForumDto dto) {
        Forum forum = Forum.builder()
                .nom(dto.getNom())
                .description(dto.getDescription())
                .categorie(dto.getCategorie())
                .build();
        return toDto(forumRepository.save(forum));
    }

    @Transactional
    public ForumDto update(Long id, UpdateForumDto dto) {
        Forum forum = forumRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found"));
        if (dto.getNom() != null) forum.setNom(dto.getNom());
        if (dto.getDescription() != null) forum.setDescription(dto.getDescription());
        if (dto.getCategorie() != null) forum.setCategorie(dto.getCategorie());
        return toDto(forumRepository.save(forum));
    }

    @Transactional
    public void delete(Long id) {
        Forum forum = forumRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found"));

        LocalDateTime now = LocalDateTime.now();

        sujetRepository.findByForum_IdAndDeletedAtIsNull(id).forEach(sujet -> {
            reponseRepository.findBySujet_IdAndDeletedAtIsNull(sujet.getId()).forEach(reponse -> {
                reponse.setDeletedAt(now);
                reponseRepository.save(reponse);
            });
            sujet.setDeletedAt(now);
            sujetRepository.save(sujet);
        });

        forum.setDeletedAt(now);
        forumRepository.save(forum);
    }

    public ForumDto toDto(Forum forum) {
        return ForumDto.builder()
                .id(forum.getId())
                .nom(forum.getNom())
                .description(forum.getDescription())
                .categorie(forum.getCategorie())
                .nbSujets(forum.getNbSujets())
                .createdAt(forum.getCreatedAt())
                .build();
    }
}
