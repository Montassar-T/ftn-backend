package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.resultat.CreateResultatDto;
import com.ftn.backend.dtos.resultat.ResultatDto;
import com.ftn.backend.dtos.resultat.UpdateResultatDto;
import com.ftn.backend.exception.business.ConflictException;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Athlete;
import com.ftn.backend.model.Epreuve;
import com.ftn.backend.model.Resultat;
import com.ftn.backend.model.User;
import com.ftn.backend.repository.AthleteRepository;
import com.ftn.backend.repository.EpreuveRepository;
import com.ftn.backend.repository.ResultatRepository;
import com.ftn.backend.repository.UserRepository;
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
public class ResultatService {

    private final ResultatRepository resultatRepository;
    private final AthleteRepository athleteRepository;
    private final EpreuveRepository epreuveRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ResultatDto getById(Long id) {
        Resultat resultat = resultatRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found"));
        return toDto(resultat);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<ResultatDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<Resultat> filters = new JpaQueryFilters<>(params, Resultat.class);
        Page<Resultat> page = resultatRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<ResultatDto> data = page.stream().map(this::toDto).toList();
        return ResponseEntity.ok(PageDto.<ResultatDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional(readOnly = true)
    public List<ResultatDto> getByAthlete(Long athleteId) {
        return resultatRepository.findByAthlete_IdAndDeletedAtIsNull(athleteId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResultatDto> getByEvent(Long eventId) {
        return resultatRepository.findByEpreuve_IdAndDeletedAtIsNull(eventId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ResultatDto create(CreateResultatDto dto) {
        Athlete athlete = athleteRepository
                .findByIdAndDeletedAtIsNull(dto.getAthleteId())
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found"));

        Epreuve epreuve = epreuveRepository
                .findByIdAndDeletedAtIsNull(dto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (resultatRepository.existsByEpreuve_IdAndAthlete_IdAndDeletedAtIsNull(
                dto.getEventId(), dto.getAthleteId())) {
            throw new ConflictException("Result already exists for this athlete and event");
        }

        User validatedBy = null;
        if (dto.getValidatedById() != null) {
            validatedBy = userRepository
                    .findByIdAndDeletedAtIsNull(dto.getValidatedById())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }

        Resultat resultat = Resultat.builder()
                .athlete(athlete)
                .epreuve(epreuve)
                .lane(dto.getLane())
                .tempsMs(dto.getTempsMs())
                .tempsDisplay(dto.getTempsDisplay())
                .pointsFina(dto.getPointsFina())
                .tour(dto.getTour())
                .rank(dto.getRank())
                .isRecord(dto.getIsRecord() != null ? dto.getIsRecord() : false)
                .validatedBy(validatedBy)
                .build();

        return toDto(resultatRepository.save(resultat));
    }

    @Transactional
    public ResultatDto update(Long id, UpdateResultatDto dto) {
        Resultat resultat = resultatRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found"));

        if (dto.getLane() != null) resultat.setLane(dto.getLane());
        if (dto.getTempsMs() != null) resultat.setTempsMs(dto.getTempsMs());
        if (dto.getTempsDisplay() != null) resultat.setTempsDisplay(dto.getTempsDisplay());
        if (dto.getPointsFina() != null) resultat.setPointsFina(dto.getPointsFina());
        if (dto.getTour() != null) resultat.setTour(dto.getTour());
        if (dto.getStatus() != null) resultat.setStatus(dto.getStatus());
        if (dto.getRank() != null) resultat.setRank(dto.getRank());
        if (dto.getIsRecord() != null) resultat.setIsRecord(dto.getIsRecord());
        if (dto.getValidatedById() != null) {
            User validatedBy = userRepository
                    .findByIdAndDeletedAtIsNull(dto.getValidatedById())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            resultat.setValidatedBy(validatedBy);
        }

        return toDto(resultatRepository.save(resultat));
    }

    @Transactional
    public void delete(Long id) {
        Resultat resultat = resultatRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found"));
        resultat.setDeletedAt(LocalDateTime.now());
        resultatRepository.save(resultat);
    }

    public ResultatDto toDto(Resultat resultat) {
        com.ftn.backend.model.Athlete athlete = resultat.getAthlete();
        com.ftn.backend.model.Epreuve epreuve = resultat.getEpreuve();

        String athleteName = null;
        String athleteNationality = null;
        Long clubId = null;
        String clubName = null;
        if (athlete != null) {
            if (athlete.getUser() != null) {
                String fn = athlete.getUser().getFirstName();
                String ln = athlete.getUser().getLastName();
                athleteName = ((fn != null ? fn : "") + " " + (ln != null ? ln : "")).trim();
            }
            athleteNationality = athlete.getNationalite();
            if (athlete.getClub() != null) {
                clubId = athlete.getClub().getId();
                clubName = athlete.getClub().getNom();
            }
        }

        String eventLabel = null;
        Long competitionId = null;
        String competitionName = null;
        if (epreuve != null) {
            eventLabel = epreuve.getDistance() + "m " + epreuve.getSwimStyle() + " " + epreuve.getGender();
            if (epreuve.getCompetition() != null) {
                competitionId = epreuve.getCompetition().getId();
                competitionName = epreuve.getCompetition().getName();
            }
        }

        return ResultatDto.builder()
                .id(resultat.getId())
                .athleteId(athlete != null ? athlete.getId() : null)
                .athleteName(athleteName)
                .athleteNationality(athleteNationality)
                .clubId(clubId)
                .clubName(clubName)
                .eventId(epreuve != null ? epreuve.getId() : null)
                .eventLabel(eventLabel)
                .competitionId(competitionId)
                .competitionName(competitionName)
                .lane(resultat.getLane())
                .tempsMs(resultat.getTempsMs())
                .tempsDisplay(resultat.getTempsDisplay())
                .pointsFina(resultat.getPointsFina())
                .tour(resultat.getTour())
                .status(resultat.getStatus())
                .rank(resultat.getRank())
                .isRecord(resultat.getIsRecord())
                .validatedById(
                        resultat.getValidatedBy() != null
                                ? resultat.getValidatedBy().getId()
                                : null)
                .createdAt(resultat.getCreatedAt())
                .build();
    }
}
