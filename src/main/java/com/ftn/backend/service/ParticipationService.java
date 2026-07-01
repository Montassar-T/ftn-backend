package com.ftn.backend.service;

import com.ftn.backend.dtos.participation.CreateParticipationDto;
import com.ftn.backend.dtos.participation.ParticipationDto;
import com.ftn.backend.enums.ParticipationStatusEnum;
import com.ftn.backend.exception.business.ConflictException;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Evenement;
import com.ftn.backend.model.Participation;
import com.ftn.backend.model.User;
import com.ftn.backend.repository.EvenementRepository;
import com.ftn.backend.repository.ParticipationRepository;
import com.ftn.backend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        List<Participation> ordered =
                participationRepository.findByEvenement_IdAndDeletedAtIsNullOrderByCreatedAtAsc(evenementId);
        return withQueuePositions(ordered);
    }

    @Transactional(readOnly = true)
    public List<ParticipationDto> getByUser(Long userId) {
        return participationRepository.findByUser_IdAndDeletedAtIsNull(userId).stream()
                .map(p -> toDto(p, null))
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ParticipationDto> getMyParticipation(Long evenementId, Long userId) {
        return participationRepository
                .findByEvenement_IdAndUser_IdAndDeletedAtIsNull(evenementId, userId)
                .map(p -> {
                    if (p.getStatus() == ParticipationStatusEnum.EN_ATTENTE) {
                        List<Participation> queue =
                                participationRepository.findByEvenement_IdAndDeletedAtIsNullOrderByCreatedAtAsc(
                                        evenementId);
                        int pos = computePosition(queue, p.getId());
                        return toDto(p, pos);
                    }
                    return toDto(p, null);
                });
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

        Participation saved = participationRepository.save(participation);
        List<Participation> queue =
                participationRepository.findByEvenement_IdAndDeletedAtIsNullOrderByCreatedAtAsc(dto.getEvenementId());
        int pos = computePosition(queue, saved.getId());
        return toDto(saved, pos);
    }

    @Transactional
    public ParticipationDto accept(Long id) {
        Participation participation = participationRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Participation not found"));

        Evenement evenement = participation.getEvenement();
        if (evenement.getCapaciteMax() != null) {
            long accepted = participationRepository.countByEvenement_IdAndStatusAndDeletedAtIsNull(
                    evenement.getId(), ParticipationStatusEnum.ACCEPTE);
            if (accepted >= evenement.getCapaciteMax()) {
                throw new ConflictException("Event has reached its maximum capacity of " + evenement.getCapaciteMax());
            }
        }

        participation.setStatus(ParticipationStatusEnum.ACCEPTE);
        return toDto(participationRepository.save(participation), null);
    }

    @Transactional
    public ParticipationDto refuse(Long id) {
        Participation participation = participationRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Participation not found"));
        participation.setStatus(ParticipationStatusEnum.REFUSE);
        return toDto(participationRepository.save(participation), null);
    }

    @Transactional
    public void delete(Long id) {
        Participation participation = participationRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Participation not found"));
        participation.setDeletedAt(LocalDateTime.now());
        participationRepository.save(participation);
    }

    private List<ParticipationDto> withQueuePositions(List<Participation> ordered) {
        List<ParticipationDto> result = new ArrayList<>();
        int queuePos = 0;
        for (Participation p : ordered) {
            Integer pos = null;
            if (p.getStatus() == ParticipationStatusEnum.EN_ATTENTE) {
                pos = ++queuePos;
            }
            result.add(toDto(p, pos));
        }
        return result;
    }

    private int computePosition(List<Participation> queue, Long targetId) {
        int pos = 0;
        for (Participation p : queue) {
            if (p.getStatus() == ParticipationStatusEnum.EN_ATTENTE) {
                pos++;
                if (p.getId().equals(targetId)) return pos;
            }
        }
        return pos;
    }

    public ParticipationDto toDto(Participation p, Integer queuePosition) {
        String userName = null;
        if (p.getUser() != null) {
            String fn = p.getUser().getFirstName();
            String ln = p.getUser().getLastName();
            userName = ((fn != null ? fn : "") + " " + (ln != null ? ln : "")).trim();
            if (userName.isEmpty()) userName = p.getUser().getEmail();
        }
        return ParticipationDto.builder()
                .id(p.getId())
                .evenementId(p.getEvenement() != null ? p.getEvenement().getId() : null)
                .evenementTitre(p.getEvenement() != null ? p.getEvenement().getTitre() : null)
                .userId(p.getUser() != null ? p.getUser().getId() : null)
                .userEmail(p.getUser() != null ? p.getUser().getEmail() : null)
                .userName(userName)
                .message(p.getMessage())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .queuePosition(queuePosition)
                .build();
    }
}
