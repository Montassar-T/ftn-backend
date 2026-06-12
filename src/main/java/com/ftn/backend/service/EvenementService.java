package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.evenement.CreateEvenementDto;
import com.ftn.backend.dtos.evenement.EvenementDto;
import com.ftn.backend.dtos.evenement.UpdateEvenementDto;
import com.ftn.backend.enums.CompetitionTypeEnum;
import com.ftn.backend.enums.EvenementStatus;
import com.ftn.backend.enums.EvenementType;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Competition;
import com.ftn.backend.model.Evenement;
import com.ftn.backend.model.User;
import com.ftn.backend.repository.CompetitionRepository;
import com.ftn.backend.repository.EvenementRepository;
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
public class EvenementService {

    private final EvenementRepository evenementRepository;
    private final CompetitionRepository competitionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<EvenementDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<Evenement> filters = new JpaQueryFilters<>(params, Evenement.class);
        Page<Evenement> page = evenementRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<EvenementDto> data = page.stream().map(this::toDto).toList();
        return ResponseEntity.ok(PageDto.<EvenementDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional(readOnly = true)
    public EvenementDto getById(Long id) {
        Evenement evenement = evenementRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evenement not found"));
        return toDto(evenement);
    }

    @Transactional
    public EvenementDto create(CreateEvenementDto dto) {
        User createdBy = null;
        if (dto.getCreatedById() != null) {
            createdBy = userRepository
                    .findByIdAndDeletedAtIsNull(dto.getCreatedById())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }

        Evenement evenement = Evenement.builder()
                .type(dto.getType())
                .titre(dto.getTitre())
                .description(dto.getDescription())
                .dateDebut(dto.getDateDebut())
                .dateFin(dto.getDateFin())
                .lieu(dto.getLieu())
                .capaciteMax(dto.getCapaciteMax())
                .createdBy(createdBy)
                .build();

        Evenement saved = evenementRepository.save(evenement);

        if (dto.getType() == EvenementType.COMPETITION) {
            Competition comp = Competition.builder()
                    .name(dto.getTitre())
                    .startDate(dto.getDateDebut().toLocalDate())
                    .endDate(dto.getDateFin().toLocalDate())
                    .code(dto.getCompetitionCode() != null ? dto.getCompetitionCode() : "")
                    .type(
                            dto.getCompetitionType() != null
                                    ? CompetitionTypeEnum.valueOf(
                                            dto.getCompetitionType().toUpperCase())
                                    : CompetitionTypeEnum.NATIONAL)
                    .lane(dto.getLane())
                    .ageCategories(dto.getAgeCategories())
                    .registrationDeadline(
                            dto.getRegistrationDeadline() != null
                                    ? dto.getRegistrationDeadline().atStartOfDay()
                                    : null)
                    .status("PLANIFIEE")
                    .evenement(saved)
                    .build();
            competitionRepository.save(comp);
        }

        return toDto(saved);
    }

    @Transactional
    public EvenementDto update(Long id, UpdateEvenementDto dto) {
        Evenement evenement = evenementRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evenement not found"));

        if (dto.getType() != null) evenement.setType(dto.getType());
        if (dto.getTitre() != null) evenement.setTitre(dto.getTitre());
        if (dto.getDescription() != null) evenement.setDescription(dto.getDescription());
        if (dto.getDateDebut() != null) evenement.setDateDebut(dto.getDateDebut());
        if (dto.getDateFin() != null) evenement.setDateFin(dto.getDateFin());
        if (dto.getLieu() != null) evenement.setLieu(dto.getLieu());
        if (dto.getCapaciteMax() != null) evenement.setCapaciteMax(dto.getCapaciteMax());
        if (dto.getStatus() != null) evenement.setStatus(dto.getStatus());

        evenementRepository.save(evenement);

        // Sync linked competition when type=COMPETITION
        if (evenement.getType() == EvenementType.COMPETITION) {
            competitionRepository.findByEvenement_IdAndDeletedAtIsNull(id).ifPresent(comp -> {
                if (dto.getTitre() != null) comp.setName(dto.getTitre());
                if (dto.getDateDebut() != null)
                    comp.setStartDate(dto.getDateDebut().toLocalDate());
                if (dto.getDateFin() != null) comp.setEndDate(dto.getDateFin().toLocalDate());
                if (dto.getCompetitionType() != null)
                    comp.setType(
                            CompetitionTypeEnum.valueOf(dto.getCompetitionType().toUpperCase()));
                if (dto.getLane() != null) comp.setLane(dto.getLane());
                if (dto.getAgeCategories() != null) comp.setAgeCategories(dto.getAgeCategories());
                if (dto.getCompetitionCode() != null) comp.setCode(dto.getCompetitionCode());
                if (dto.getRegistrationDeadline() != null)
                    comp.setRegistrationDeadline(dto.getRegistrationDeadline().atStartOfDay());
                if (dto.getStatus() != null) comp.setStatus(toCompetitionStatus(dto.getStatus()));
                competitionRepository.save(comp);
            });
        }

        return toDto(evenement);
    }

    @Transactional
    public void delete(Long id) {
        Evenement evenement = evenementRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evenement not found"));

        // Cascade soft-delete to linked competition
        if (evenement.getType() == EvenementType.COMPETITION) {
            competitionRepository.findByEvenement_IdAndDeletedAtIsNull(id).ifPresent(comp -> {
                comp.setDeletedAt(LocalDateTime.now());
                competitionRepository.save(comp);
            });
        }

        evenement.setDeletedAt(LocalDateTime.now());
        evenementRepository.save(evenement);
    }

    public EvenementDto toDto(Evenement e) {
        String createdByName = null;
        Long createdById = null;
        if (e.getCreatedBy() != null) {
            createdById = e.getCreatedBy().getId();
            String fn = e.getCreatedBy().getFirstName();
            String ln = e.getCreatedBy().getLastName();
            createdByName = ((fn != null ? fn : "") + " " + (ln != null ? ln : "")).trim();
        }

        Long competitionId = null;
        if (e.getType() == EvenementType.COMPETITION) {
            competitionId = competitionRepository
                    .findByEvenement_IdAndDeletedAtIsNull(e.getId())
                    .map(Competition::getId)
                    .orElse(null);
        }

        return EvenementDto.builder()
                .id(e.getId())
                .type(e.getType())
                .titre(e.getTitre())
                .description(e.getDescription())
                .dateDebut(e.getDateDebut())
                .dateFin(e.getDateFin())
                .lieu(e.getLieu())
                .capaciteMax(e.getCapaciteMax())
                .status(e.getStatus())
                .createdById(createdById)
                .createdByName(createdByName)
                .createdAt(e.getCreatedAt())
                .competitionId(competitionId)
                .build();
    }

    /** Maps EvenementStatus → Competition status string */
    private String toCompetitionStatus(EvenementStatus status) {
        if (status == null) return "PLANIFIEE";
        return switch (status) {
            case EN_COURS -> "EN_COURS";
            case TERMINE, ARCHIVE -> "TERMINEE";
            default -> "PLANIFIEE";
        };
    }
}
