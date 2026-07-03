package com.ftn.backend.service;

import com.ftn.backend.dtos.forum.CreateReactionDto;
import com.ftn.backend.dtos.forum.ForumReactionDto;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.ForumReaction;
import com.ftn.backend.model.Reponse;
import com.ftn.backend.model.Sujet;
import com.ftn.backend.model.User;
import com.ftn.backend.repository.ForumReactionRepository;
import com.ftn.backend.repository.ReponseRepository;
import com.ftn.backend.repository.SujetRepository;
import com.ftn.backend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ForumReactionService {

    private final ForumReactionRepository reactionRepository;
    private final SujetRepository sujetRepository;
    private final ReponseRepository reponseRepository;
    private final UserRepository userRepository;

    @Transactional
    public ForumReactionDto react(CreateReactionDto dto) {
        User user = userRepository
                .findByIdAndDeletedAtIsNull(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Sujet thread = dto.getThreadId() != null
                ? sujetRepository.findByIdAndDeletedAtIsNull(dto.getThreadId()).orElse(null)
                : null;
        Reponse post = dto.getPostId() != null
                ? reponseRepository.findByIdAndDeletedAtIsNull(dto.getPostId()).orElse(null)
                : null;
        ForumReaction reaction = ForumReaction.builder()
                .thread(thread)
                .post(post)
                .user(user)
                .type(dto.getType())
                .build();
        return toDto(reactionRepository.save(reaction));
    }

    @Transactional
    public void delete(Long id) {
        ForumReaction reaction = reactionRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reaction not found"));
        reaction.setDeletedAt(LocalDateTime.now());
        reactionRepository.save(reaction);
    }

    @Transactional(readOnly = true)
    public List<ForumReactionDto> getForSujet(Long sujetId) {
        List<ForumReaction> threadReactions = reactionRepository.findByThread_IdAndDeletedAtIsNull(sujetId);
        List<ForumReaction> postReactions = reactionRepository.findByPost_Sujet_IdAndDeletedAtIsNull(sujetId);
        return Stream.concat(threadReactions.stream(), postReactions.stream())
                .map(this::toDto)
                .toList();
    }

    public ForumReactionDto toDto(ForumReaction r) {
        return ForumReactionDto.builder()
                .id(r.getId())
                .threadId(r.getThread() != null ? r.getThread().getId() : null)
                .postId(r.getPost() != null ? r.getPost().getId() : null)
                .userId(r.getUser().getId())
                .userEmail(r.getUser().getEmail())
                .type(r.getType())
                .build();
    }
}
