package com.ftn.backend.service;

import com.ftn.backend.dtos.participation.CreateParticipationDto;
import com.ftn.backend.dtos.participation.ParticipationDto;
import com.ftn.backend.enums.ParticipationStatus;
import com.ftn.backend.exception.business.ConflictException;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Evenement;
import com.ftn.backend.model.Participation;
import com.ftn.backend.model.User;
import com.ftn.backend.repository.EvenementRepository;
import com.ftn.backend.repository.ParticipationRepository;
import com.ftn.backend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final EvenementRepository evenementRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ParticipationDto> getByEvenement(Long evenementId) {
        return participationRepository.findByEvenement_IdAndDeletedAtIsNull(evenementId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ParticipationDto> getByUser(Long userId) {
        return participationRepository.findByUser_IdAndDeletedAtIsNull(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ParticipationDto create(CreateParticipationDto dto) {
        Evenement evenement = evenementRepository
                .findByIdAndDeletedAtIsNull(dto.getEvenementId())
                .orElseThrow(() -> new ResourceNotFoundException("Evenement not found"));

        User user = userRepository
                .findByIdAndDeletedAtIsNull(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (participationRepository.existsByEvenement_IdAndUser_IdAndDeletedAtIsNull(
                dto.getEvenementId(), dto.getUserId())) {
            throw new ConflictException("User already registered to this event");
        }

        Participation participation = Participation.builder()
                .evenement(evenement)
                .user(user)
                .message(dto.getMessage())
                .build();

        return toDto(participationRepository.save(participation));
    }

    @Transactional
    public ParticipationDto updateStatus(Long id, ParticipationStatus status) {
        Participation participation = participationRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Participation not found"));
        participation.setStatus(status);
        return toDto(participationRepository.save(participation));
    }

    @Transactional
    public void delete(Long id) {
        Participation participation = participationRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Participation not found"));
        participation.setDeletedAt(LocalDateTime.now());
        participationRepository.save(participation);
    }

    public ParticipationDto toDto(Participation p) {
        String userName = null;
        Long userId = null;
        if (p.getUser() != null) {
            userId = p.getUser().getId();
            String fn = p.getUser().getFirstName();
            String ln = p.getUser().getLastName();
            userName = ((fn != null ? fn : "") + " " + (ln != null ? ln : "")).trim();
        }
        return ParticipationDto.builder()
                .id(p.getId())
                .evenementId(p.getEvenement() != null ? p.getEvenement().getId() : null)
                .evenementTitre(p.getEvenement() != null ? p.getEvenement().getTitre() : null)
                .userId(userId)
                .userName(userName)
                .message(p.getMessage())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
