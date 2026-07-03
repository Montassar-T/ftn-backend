package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.licence.CreateLicenceDto;
import com.ftn.backend.dtos.licence.LicenceDto;
import com.ftn.backend.dtos.licence.UpdateLicenceDto;
import com.ftn.backend.enums.StatutLicenceEnum;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Athlete;
import com.ftn.backend.model.Club;
import com.ftn.backend.model.Licence;
import com.ftn.backend.repository.AthleteRepository;
import com.ftn.backend.repository.ClubRepository;
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
public class LicenceService {

    private final LicenceRepository licenceRepository;
    private final AthleteRepository athleteRepository;
    private final ClubRepository clubRepository;

    @Transactional(readOnly = true)
    public LicenceDto getById(Long id) {
        Licence licence = licenceRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Licence not found"));
        return toDto(licence);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<LicenceDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<Licence> filters = new JpaQueryFilters<>(params, Licence.class);
        Page<Licence> page = licenceRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<LicenceDto> data = page.stream().map(this::toDto).toList();
        return ResponseEntity.ok(PageDto.<LicenceDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional
    public LicenceDto create(CreateLicenceDto dto) {
        Athlete athlete = athleteRepository
                .findByIdAndDeletedAtIsNull(dto.getAthleteId())
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found"));
        Club club = clubRepository
                .findByIdAndDeletedAtIsNull(dto.getClubId())
                .orElseThrow(() -> new ResourceNotFoundException("Club not found"));

        Licence licence = Licence.builder()
                .athlete(athlete)
                .club(club)
                .numero(dto.getNumero())
                .type(dto.getType())
                .dateDebut(dto.getDateDebut())
                .dateExpiration(dto.getDateExpiration())
                .build();

        return toDto(licenceRepository.save(licence));
    }

    @Transactional
    public LicenceDto update(Long id, UpdateLicenceDto dto) {
        Licence licence = licenceRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Licence not found"));
        if (dto.getNumero() != null) licence.setNumero(dto.getNumero());
        if (dto.getType() != null) licence.setType(dto.getType());
        if (dto.getDateDebut() != null) licence.setDateDebut(dto.getDateDebut());
        if (dto.getDateExpiration() != null) licence.setDateExpiration(dto.getDateExpiration());
        return toDto(licenceRepository.save(licence));
    }

    @Transactional
    public void delete(Long id) {
        Licence licence = licenceRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Licence not found"));
        licence.setDeletedAt(LocalDateTime.now());
        licenceRepository.save(licence);
    }

    @Transactional
    public LicenceDto valider(Long id) {
        Licence licence = licenceRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Licence not found"));
        licence.setStatut(StatutLicenceEnum.VALIDEE);
        return toDto(licenceRepository.save(licence));
    }

    @Transactional
    public LicenceDto rejeter(Long id) {
        Licence licence = licenceRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Licence not found"));
        licence.setStatut(StatutLicenceEnum.REJETEE);
        return toDto(licenceRepository.save(licence));
    }

    @Transactional(readOnly = true)
    public List<LicenceDto> getByAthlete(Long athleteId) {
        return licenceRepository.findByAthlete_IdAndDeletedAtIsNull(athleteId).stream()
                .map(this::toDto)
                .toList();
    }

    public LicenceDto toDto(Licence licence) {
        var athlete = licence.getAthlete();
        String athleteName = null;
        if (athlete != null) {
            String nom = athlete.getNom();
            String prenom = athlete.getPrenom();
            athleteName = ((prenom != null ? prenom : "") + " " + (nom != null ? nom : "")).trim();
            if ((athleteName == null || athleteName.isEmpty()) && athlete.getUser() != null) {
                String fn = athlete.getUser().getFirstName();
                String ln = athlete.getUser().getLastName();
                athleteName = ((fn != null ? fn : "") + " " + (ln != null ? ln : "")).trim();
            }
        }
        return LicenceDto.builder()
                .id(licence.getId())
                .athleteId(licence.getAthlete().getId())
                .athleteName(athleteName)
                .clubName(licence.getClub() != null ? licence.getClub().getNom() : null)
                .clubId(licence.getClub().getId())
                .numero(licence.getNumero())
                .type(licence.getType())
                .dateDebut(licence.getDateDebut())
                .dateExpiration(licence.getDateExpiration())
                .statut(licence.getStatut())
                .createdAt(licence.getCreatedAt())
                .build();
    }
}
