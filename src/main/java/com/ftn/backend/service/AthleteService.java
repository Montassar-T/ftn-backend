package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.athlete.AthleteDto;
import com.ftn.backend.dtos.athlete.CreateAthleteDto;
import com.ftn.backend.dtos.athlete.UpdateAthleteDto;
import com.ftn.backend.dtos.licence.LicenceDto;
import com.ftn.backend.enums.CategorieEnum;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Athlete;
import com.ftn.backend.model.Club;
import com.ftn.backend.model.User;
import com.ftn.backend.repository.AthleteRepository;
import com.ftn.backend.repository.ClubRepository;
import com.ftn.backend.repository.LicenceRepository;
import com.ftn.backend.repository.UserRepository;
import com.ftn.backend.utils.JpaQueryFilters;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AthleteService {

    private final AthleteRepository athleteRepository;
    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final LicenceRepository licenceRepository;

    @Transactional(readOnly = true)
    public AthleteDto getById(Long id) {
        Athlete athlete = athleteRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found"));
        return toDto(athlete);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<AthleteDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<Athlete> filters = new JpaQueryFilters<>(params, Athlete.class);
        Page<Athlete> page = athleteRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<AthleteDto> data = page.stream().map(this::toDto).toList();
        return ResponseEntity.ok(PageDto.<AthleteDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional
    public AthleteDto create(CreateAthleteDto dto) {
        CategorieEnum categorie =
                dto.getCategorie() != null ? dto.getCategorie() : deriveCategorie(dto.getDateNaissance());
        Athlete.AthleteBuilder builder = Athlete.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .dateNaissance(dto.getDateNaissance())
                .nationalite(dto.getNationalite())
                .categorie(categorie)
                .sexe(dto.getSexe());

        if (dto.getClubId() != null) {
            Club club = clubRepository
                    .findByIdAndDeletedAtIsNull(dto.getClubId())
                    .orElseThrow(() -> new ResourceNotFoundException("Club not found"));
            builder.club(club);
        }

        if (dto.getUserId() != null) {
            User user = userRepository
                    .findByIdAndDeletedAtIsNull(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            builder.user(user);
        }

        return toDto(athleteRepository.save(builder.build()));
    }

    @Transactional
    public AthleteDto update(Long id, UpdateAthleteDto dto) {
        Athlete athlete = athleteRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found"));

        if (dto.getNom() != null) athlete.setNom(dto.getNom());
        if (dto.getPrenom() != null) athlete.setPrenom(dto.getPrenom());
        if (dto.getClubId() != null) {
            Club club = clubRepository
                    .findByIdAndDeletedAtIsNull(dto.getClubId())
                    .orElseThrow(() -> new ResourceNotFoundException("Club not found"));
            athlete.setClub(club);
        }
        if (dto.getDateNaissance() != null) athlete.setDateNaissance(dto.getDateNaissance());
        if (dto.getNationalite() != null) athlete.setNationalite(dto.getNationalite());
        if (dto.getCategorie() != null) athlete.setCategorie(dto.getCategorie());
        if (dto.getSexe() != null) athlete.setSexe(dto.getSexe());
        if (athlete.getCategorie() == null && athlete.getDateNaissance() != null) {
            athlete.setCategorie(deriveCategorie(athlete.getDateNaissance()));
        }

        return toDto(athleteRepository.save(athlete));
    }

    @Transactional
    public void delete(Long id) {
        Athlete athlete = athleteRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found"));
        athlete.setDeletedAt(LocalDateTime.now());
        athleteRepository.save(athlete);
    }

    @Transactional(readOnly = true)
    public List<LicenceDto> getLicences(Long athleteId) {
        athleteRepository
                .findByIdAndDeletedAtIsNull(athleteId)
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found"));
        return licenceRepository.findByAthlete_IdAndDeletedAtIsNull(athleteId).stream()
                .map(l -> LicenceDto.builder()
                        .id(l.getId())
                        .athleteId(l.getAthlete().getId())
                        .clubId(l.getClub().getId())
                        .numero(l.getNumero())
                        .type(l.getType())
                        .dateDebut(l.getDateDebut())
                        .dateExpiration(l.getDateExpiration())
                        .statut(l.getStatut())
                        .createdAt(l.getCreatedAt())
                        .build())
                .toList();
    }

    public AthleteDto toDto(Athlete athlete) {
        String nom = athlete.getNom();
        String prenom = athlete.getPrenom();
        if ((nom == null || nom.isBlank()) && athlete.getUser() != null) {
            nom = athlete.getUser().getLastName();
        }
        if ((prenom == null || prenom.isBlank()) && athlete.getUser() != null) {
            prenom = athlete.getUser().getFirstName();
        }
        return AthleteDto.builder()
                .id(athlete.getId())
                .nom(nom)
                .prenom(prenom)
                .userId(athlete.getUser() != null ? athlete.getUser().getId() : null)
                .clubId(athlete.getClub() != null ? athlete.getClub().getId() : null)
                .clubNom(athlete.getClub() != null ? athlete.getClub().getNom() : null)
                .dateNaissance(athlete.getDateNaissance())
                .nationalite(athlete.getNationalite())
                .categorie(
                        athlete.getCategorie() != null
                                ? athlete.getCategorie()
                                : deriveCategorie(athlete.getDateNaissance()))
                .sexe(athlete.getSexe())
                .createdAt(athlete.getCreatedAt())
                .build();
    }

    private CategorieEnum deriveCategorie(LocalDate dateNaissance) {
        if (dateNaissance == null) return null;
        int age = Year.now().getValue() - dateNaissance.getYear();
        if (age <= 10) return CategorieEnum.POUSSIN;
        if (age <= 12) return CategorieEnum.BENJAMIN;
        if (age <= 14) return CategorieEnum.MINIME;
        if (age <= 16) return CategorieEnum.CADET;
        if (age <= 18) return CategorieEnum.JUNIOR;
        return CategorieEnum.SENIOR;
    }
}
