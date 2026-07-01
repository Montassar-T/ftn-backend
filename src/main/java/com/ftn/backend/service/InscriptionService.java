package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.inscription.CreateInscriptionDto;
import com.ftn.backend.dtos.inscription.InscriptionDto;
import com.ftn.backend.enums.StatutLicenceEnum;
import com.ftn.backend.exception.business.ConflictException;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Athlete;
import com.ftn.backend.model.Epreuve;
import com.ftn.backend.model.Inscription;
import com.ftn.backend.repository.AthleteRepository;
import com.ftn.backend.repository.EpreuveRepository;
import com.ftn.backend.repository.InscriptionRepository;
import com.ftn.backend.repository.LicenceRepository;
import com.ftn.backend.utils.JpaQueryFilters;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final AthleteRepository athleteRepository;
    private final EpreuveRepository epreuveRepository;
    private final LicenceRepository licenceRepository;

    @Transactional(readOnly = true)
    public InscriptionDto getById(Long id) {
        return toDto(
                inscriptionRepository
                        .findByIdAndDeletedAtIsNull(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Registration not found")),
                null);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<InscriptionDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<Inscription> filters = new JpaQueryFilters<>(params, Inscription.class);
        Page<Inscription> page = inscriptionRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<InscriptionDto> data = page.stream().map(i -> toDto(i, null)).toList();
        return ResponseEntity.ok(PageDto.<InscriptionDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional
    public InscriptionDto create(CreateInscriptionDto dto) {
        Athlete athlete = athleteRepository
                .findByIdAndDeletedAtIsNull(dto.getAthleteId())
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found"));
        Epreuve epreuve = epreuveRepository
                .findByIdAndDeletedAtIsNull(dto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (inscriptionRepository.existsByAthlete_IdAndEpreuve_IdAndDeletedAtIsNull(athlete.getId(), epreuve.getId())) {
            throw new ConflictException("Athlete already registered for this event");
        }

        boolean licenceActive = licenceRepository.findByAthlete_IdAndDeletedAtIsNull(athlete.getId()).stream()
                .anyMatch(l -> l.getStatut() == StatutLicenceEnum.VALIDEE
                        && (l.getDateExpiration() == null
                                || !l.getDateExpiration().isBefore(java.time.LocalDate.now())));
        if (!licenceActive) {
            throw new ConflictException("Athlete has no active licence");
        }

        Inscription inscription = Inscription.builder()
                .athlete(athlete)
                .epreuve(epreuve)
                .seedTime(dto.getSeedTime())
                .registeredAt(LocalDateTime.now())
                .build();

        Inscription saved = inscriptionRepository.save(inscription);
        List<Inscription> queue =
                inscriptionRepository.findByEpreuve_IdAndDeletedAtIsNullOrderByRegisteredAtAsc(epreuve.getId());
        int pos = computePosition(queue, saved.getId());
        return toDto(saved, pos);
    }

    @Transactional
    public void delete(Long id) {
        Inscription inscription = inscriptionRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        inscription.setDeletedAt(LocalDateTime.now());
        inscriptionRepository.save(inscription);
    }

    @Transactional
    public InscriptionDto valider(Long id) {
        Inscription inscription = inscriptionRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        inscription.setStatus("VALIDEE");
        return toDto(inscriptionRepository.save(inscription), null);
    }

    @Transactional
    public InscriptionDto annuler(Long id) {
        Inscription inscription = inscriptionRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        inscription.setStatus("ANNULEE");
        return toDto(inscriptionRepository.save(inscription), null);
    }

    @Transactional(readOnly = true)
    public List<InscriptionDto> getByAthlete(Long athleteId) {
        return inscriptionRepository.findByAthlete_IdAndDeletedAtIsNull(athleteId).stream()
                .map(i -> toDto(i, null))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InscriptionDto> getByEvent(Long eventId) {
        List<Inscription> ordered =
                inscriptionRepository.findByEpreuve_IdAndDeletedAtIsNullOrderByRegisteredAtAsc(eventId);
        return withQueuePositions(ordered);
    }

    @Transactional(readOnly = true)
    public List<InscriptionDto> getByCompetition(Long competitionId) {
        List<Inscription> ordered =
                inscriptionRepository.findByEpreuve_Competition_IdAndDeletedAtIsNullOrderByRegisteredAtAsc(
                        competitionId);
        return withQueuePositions(ordered);
    }

    private List<InscriptionDto> withQueuePositions(List<Inscription> ordered) {
        List<InscriptionDto> result = new ArrayList<>();
        int queuePos = 0;
        for (Inscription i : ordered) {
            Integer pos = null;
            if ("EN_ATTENTE".equals(i.getStatus())) {
                pos = ++queuePos;
            }
            result.add(toDto(i, pos));
        }
        return result;
    }

    private int computePosition(List<Inscription> queue, Long targetId) {
        int pos = 0;
        for (Inscription i : queue) {
            if ("EN_ATTENTE".equals(i.getStatus())) {
                pos++;
                if (i.getId().equals(targetId)) return pos;
            }
        }
        return pos;
    }

    public InscriptionDto toDto(Inscription inscription, Integer queuePosition) {
        String athleteNom = null;
        String athletePrenom = null;
        if (inscription.getAthlete() != null) {
            athleteNom = inscription.getAthlete().getNom();
            athletePrenom = inscription.getAthlete().getPrenom();
        }
        String epreuveLabel = null;
        Long competitionId = null;
        if (inscription.getEpreuve() != null) {
            Epreuve e = inscription.getEpreuve();
            epreuveLabel = e.getDistance() + "m " + e.getSwimStyle();
            if (e.getCompetition() != null) competitionId = e.getCompetition().getId();
        }
        return InscriptionDto.builder()
                .id(inscription.getId())
                .athleteId(inscription.getAthlete().getId())
                .eventId(inscription.getEpreuve().getId())
                .seedTime(inscription.getSeedTime())
                .status(inscription.getStatus())
                .registeredAt(inscription.getRegisteredAt())
                .createdAt(inscription.getCreatedAt())
                .athleteNom(athleteNom)
                .athletePrenom(athletePrenom)
                .epreuveLabel(epreuveLabel)
                .queuePosition(queuePosition)
                .competitionId(competitionId)
                .build();
    }
}
