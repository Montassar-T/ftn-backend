package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.resultat.AthleteStatsDto;
import com.ftn.backend.dtos.resultat.AthleteStatsDto.PersonalBestDto;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final ClassementService classementService;
    private final FinaPointsService finaPointsService;

    @Transactional(readOnly = true)
    public ResultatDto getById(Long id) {
        return toDto(resultatRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found")));
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
                .map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ResultatDto> getByEvent(Long eventId) {
        return resultatRepository.findByEpreuve_IdAndDeletedAtIsNull(eventId).stream()
                .map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ResultatDto> getByCompetition(Long competitionId) {
        return resultatRepository.findByEpreuve_Competition_IdAndDeletedAtIsNull(competitionId).stream()
                .sorted(Comparator.comparing(r -> r.getEpreuve().getId()))
                .map(this::toDto).toList();
    }

    // ─── CRUD ────────────────────────────────────────────────────────────────

    @Transactional
    public ResultatDto create(CreateResultatDto dto) {
        Athlete athlete = athleteRepository.findByIdAndDeletedAtIsNull(dto.getAthleteId())
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found"));
        Epreuve epreuve = epreuveRepository.findByIdAndDeletedAtIsNull(dto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (resultatRepository.existsByEpreuve_IdAndAthlete_IdAndDeletedAtIsNull(dto.getEventId(), dto.getAthleteId())) {
            throw new ConflictException("Result already exists for this athlete and event");
        }

        // Auto-calcul FINA si temps fourni et points non renseignés
        BigDecimal pointsFina = dto.getPointsFina();
        if (pointsFina == null && dto.getTempsMs() != null) {
            pointsFina = finaPointsService.calculate(
                    epreuve.getSwimStyle(), epreuve.getDistance(), epreuve.getGender(), dto.getTempsMs());
        }

        Resultat resultat = Resultat.builder()
                .athlete(athlete)
                .epreuve(epreuve)
                .lane(dto.getLane())
                .tempsMs(dto.getTempsMs())
                .tempsDisplay(dto.getTempsDisplay())
                .pointsFina(pointsFina)
                .tour(dto.getTour())
                .rank(dto.getRank())
                .isRecord(false)  // calculé après validation
                .build();

        return toDto(resultatRepository.save(resultat));
    }

    @Transactional
    public ResultatDto update(Long id, UpdateResultatDto dto) {
        Resultat resultat = resultatRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found"));

        String previousStatus = resultat.getStatus();

        if (dto.getLane() != null) resultat.setLane(dto.getLane());
        if (dto.getTempsDisplay() != null) resultat.setTempsDisplay(dto.getTempsDisplay());
        if (dto.getTour() != null) resultat.setTour(dto.getTour());
        if (dto.getStatus() != null) resultat.setStatus(dto.getStatus());
        if (dto.getRank() != null) resultat.setRank(dto.getRank());
        if (dto.getIsRecord() != null) resultat.setIsRecord(dto.getIsRecord());

        // Auto-calcul FINA si temps modifié
        if (dto.getTempsMs() != null) {
            resultat.setTempsMs(dto.getTempsMs());
            if (dto.getPointsFina() == null && resultat.getEpreuve() != null) {
                Epreuve ep = resultat.getEpreuve();
                BigDecimal pts = finaPointsService.calculate(
                        ep.getSwimStyle(), ep.getDistance(), ep.getGender(), dto.getTempsMs());
                if (pts != null) resultat.setPointsFina(pts);
            }
        }
        if (dto.getPointsFina() != null) resultat.setPointsFina(dto.getPointsFina());

        if (dto.getValidatedById() != null) {
            User user = userRepository.findByIdAndDeletedAtIsNull(dto.getValidatedById())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            resultat.setValidatedBy(user);
        }

        Resultat saved = resultatRepository.save(resultat);

        // Après validation → détection record + reconstruction classement
        if ("VALIDE".equals(dto.getStatus()) && !"VALIDE".equals(previousStatus)) {
            updateRecordFlag(saved);
            triggerRebuild(saved);
        }

        return toDto(saved);
    }

    @Transactional
    public ResultatDto valider(Long id) {
        Resultat resultat = resultatRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found"));
        resultat.setStatus("VALIDE");
        Resultat saved = resultatRepository.save(resultat);
        updateRecordFlag(saved);
        triggerRebuild(saved);
        return toDto(saved);
    }

    @Transactional
    public ResultatDto rejeter(Long id) {
        Resultat resultat = resultatRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found"));
        resultat.setStatus("REJETE");
        return toDto(resultatRepository.save(resultat));
    }

    @Transactional
    public void delete(Long id) {
        Resultat resultat = resultatRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found"));
        resultat.setDeletedAt(LocalDateTime.now());
        resultatRepository.save(resultat);
    }

    // ─── STATS ATHLÈTE ───────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public AthleteStatsDto getAthleteStats(Long athleteId) {
        Athlete athlete = athleteRepository.findByIdAndDeletedAtIsNull(athleteId)
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found"));

        List<Resultat> all = resultatRepository.findByAthlete_IdAndDeletedAtIsNull(athleteId);

        // Meilleur temps par épreuve (personal bests)
        Map<Long, Resultat> bestByEpreuve = new java.util.HashMap<>();
        for (Resultat r : all) {
            if (r.getTempsMs() == null) continue;
            Long eid = r.getEpreuve().getId();
            if (!bestByEpreuve.containsKey(eid) || r.getTempsMs() < bestByEpreuve.get(eid).getTempsMs()) {
                bestByEpreuve.put(eid, r);
            }
        }

        List<PersonalBestDto> personalBests = bestByEpreuve.values().stream()
                .sorted(Comparator.comparing(r -> r.getEpreuve().getSwimStyle()))
                .map(r -> PersonalBestDto.builder()
                        .eventId(r.getEpreuve().getId())
                        .eventLabel(r.getEpreuve().getDistance() + "m " + r.getEpreuve().getSwimStyle() + " " + r.getEpreuve().getGender())
                        .tempsDisplay(r.getTempsDisplay())
                        .tempsMs(r.getTempsMs())
                        .pointsFina(r.getPointsFina())
                        .rank(r.getRank())
                        .isRecord(r.getIsRecord())
                        .competitionName(r.getEpreuve().getCompetition() != null ? r.getEpreuve().getCompetition().getName() : null)
                        .build())
                .toList();

        BigDecimal bestFina = all.stream()
                .filter(r -> r.getPointsFina() != null)
                .map(Resultat::getPointsFina)
                .max(Comparator.naturalOrder())
                .orElse(null);

        Integer bestRank = all.stream()
                .filter(r -> r.getRank() != null)
                .map(Resultat::getRank)
                .min(Comparator.naturalOrder())
                .orElse(null);

        String athleteName = null;
        if (athlete.getUser() != null) {
            athleteName = ((athlete.getUser().getFirstName() != null ? athlete.getUser().getFirstName() : "") + " "
                    + (athlete.getUser().getLastName() != null ? athlete.getUser().getLastName() : "")).trim();
        }

        return AthleteStatsDto.builder()
                .athleteId(athleteId)
                .athleteName(athleteName)
                .totalResults(all.size())
                .validated((int) all.stream().filter(r -> "VALIDE".equals(r.getStatus())).count())
                .records((int) all.stream().filter(r -> Boolean.TRUE.equals(r.getIsRecord())).count())
                .bestPointsFina(bestFina)
                .bestRank(bestRank)
                .personalBests(personalBests)
                .build();
    }

    // ─── MÉTHODES PRIVÉES ────────────────────────────────────────────────────

    private void updateRecordFlag(Resultat validated) {
        if (validated.getTempsMs() == null) return;
        Long epreuveId = validated.getEpreuve().getId();

        // Meilleur temps validé pour cette épreuve (hors le résultat courant)
        Optional<Integer> currentBest = resultatRepository.findByEpreuve_IdAndDeletedAtIsNull(epreuveId).stream()
                .filter(r -> "VALIDE".equals(r.getStatus()) && !r.getId().equals(validated.getId()) && r.getTempsMs() != null)
                .map(Resultat::getTempsMs)
                .min(Integer::compareTo);

        boolean isNewRecord = currentBest.isEmpty() || validated.getTempsMs() < currentBest.get();

        if (isNewRecord) {
            // Retirer isRecord des anciens records de cette épreuve
            resultatRepository.findByEpreuve_IdAndDeletedAtIsNull(epreuveId).stream()
                    .filter(r -> Boolean.TRUE.equals(r.getIsRecord()) && !r.getId().equals(validated.getId()))
                    .forEach(r -> { r.setIsRecord(false); resultatRepository.save(r); });
            validated.setIsRecord(true);
            resultatRepository.save(validated);
        }
    }

    private void triggerRebuild(Resultat resultat) {
        try {
            Epreuve ep = resultat.getEpreuve();
            if (ep == null || ep.getScheduledDate() == null) return;
            String season = String.valueOf(ep.getScheduledDate().getYear());
            classementService.rebuild(ep.getId(), season);
        } catch (Exception e) {
            // Non-bloquant : la reconstruction échoue silencieusement
        }
    }

    // ─── MAPPING ─────────────────────────────────────────────────────────────

    public ResultatDto toDto(Resultat r) {
        Athlete athlete = r.getAthlete();
        Epreuve epreuve = r.getEpreuve();

        String athleteName = null;
        String athleteNationality = null;
        Long clubId = null;
        String clubName = null;
        if (athlete != null) {
            // Use nom/prenom directly from Athlete; fallback to linked User if those fields are blank
            String nom = athlete.getNom();
            String prenom = athlete.getPrenom();
            if (nom != null || prenom != null) {
                athleteName = ((prenom != null ? prenom : "") + " " + (nom != null ? nom : "")).trim();
            } else if (athlete.getUser() != null) {
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
                .id(r.getId())
                .athleteId(athlete != null ? athlete.getId() : null)
                .athleteName(athleteName)
                .athleteNationality(athleteNationality)
                .clubId(clubId)
                .clubName(clubName)
                .eventId(epreuve != null ? epreuve.getId() : null)
                .eventLabel(eventLabel)
                .competitionId(competitionId)
                .competitionName(competitionName)
                .lane(r.getLane())
                .tempsMs(r.getTempsMs())
                .tempsDisplay(r.getTempsDisplay())
                .pointsFina(r.getPointsFina())
                .tour(r.getTour())
                .status(r.getStatus())
                .rank(r.getRank())
                .isRecord(r.getIsRecord())
                .validatedById(r.getValidatedBy() != null ? r.getValidatedBy().getId() : null)
                .createdAt(r.getCreatedAt())
                .build();
    }
}
