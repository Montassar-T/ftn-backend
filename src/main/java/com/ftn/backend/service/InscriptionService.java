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
        Inscription inscription = inscriptionRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        return toDto(inscription);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<InscriptionDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<Inscription> filters = new JpaQueryFilters<>(params, Inscription.class);
        Page<Inscription> page = inscriptionRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<InscriptionDto> data = page.stream().map(this::toDto).toList();
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

        boolean licenceActive = licenceRepository.findByAthlete_IdAndDeletedAtIsNull(athlete.getId()).stream()
                .anyMatch(licence -> licence.getStatut() == StatutLicenceEnum.VALIDEE
                        && (licence.getDateExpiration() == null
                                || !licence.getDateExpiration().isBefore(java.time.LocalDate.now())));
        if (!licenceActive) {
            throw new ConflictException("Athlete has no active licence");
        }

        Inscription inscription = Inscription.builder()
                .athlete(athlete)
                .epreuve(epreuve)
                .seedTime(dto.getSeedTime())
                .registeredAt(LocalDateTime.now())
                .build();

        return toDto(inscriptionRepository.save(inscription));
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
        return toDto(inscriptionRepository.save(inscription));
    }

    @Transactional
    public InscriptionDto annuler(Long id) {
        Inscription inscription = inscriptionRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        inscription.setStatus("ANNULEE");
        return toDto(inscriptionRepository.save(inscription));
    }

    @Transactional(readOnly = true)
    public List<InscriptionDto> getByAthlete(Long athleteId) {
        return inscriptionRepository.findByAthlete_IdAndDeletedAtIsNull(athleteId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InscriptionDto> getByEvent(Long eventId) {
        return inscriptionRepository.findByEpreuve_IdAndDeletedAtIsNull(eventId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InscriptionDto> getByCompetition(Long competitionId) {
        return inscriptionRepository.findByEpreuve_Competition_IdAndDeletedAtIsNull(competitionId).stream()
                .map(this::toDto)
                .toList();
    }

    public InscriptionDto toDto(Inscription inscription) {
        return InscriptionDto.builder()
                .id(inscription.getId())
                .athleteId(inscription.getAthlete().getId())
                .eventId(inscription.getEpreuve().getId())
                .seedTime(inscription.getSeedTime())
                .status(inscription.getStatus())
                .registeredAt(inscription.getRegisteredAt())
                .createdAt(inscription.getCreatedAt())
                .build();
    }
}
