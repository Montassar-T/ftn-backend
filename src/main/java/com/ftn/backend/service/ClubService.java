package com.ftn.backend.service;

import com.ftn.backend.dtos.PageDto;
import com.ftn.backend.dtos.athlete.AthleteDto;
import com.ftn.backend.dtos.club.ClubDto;
import com.ftn.backend.dtos.club.ClubStaffDto;
import com.ftn.backend.dtos.club.CreateClubDto;
import com.ftn.backend.dtos.club.CreateClubStaffDto;
import com.ftn.backend.dtos.club.UpdateClubDto;
import com.ftn.backend.enums.PosteStaffEnum;
import com.ftn.backend.exception.business.ConflictException;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Club;
import com.ftn.backend.model.ClubStaff;
import com.ftn.backend.model.User;
import com.ftn.backend.repository.AthleteRepository;
import com.ftn.backend.repository.ClubRepository;
import com.ftn.backend.repository.ClubStaffRepository;
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
public class ClubService {

    private final ClubRepository clubRepository;
    private final AthleteRepository athleteRepository;
    private final AthleteService athleteService;
    private final ClubStaffRepository clubStaffRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ClubDto getById(Long id) {
        Club club = clubRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found"));
        return toDto(club);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<ClubDto>> getAll(Map<String, String> params) {
        JpaQueryFilters<Club> filters = new JpaQueryFilters<>(params, Club.class);
        Page<Club> page = clubRepository.findAll(filters.getSpecification(), filters.getPageable());
        List<ClubDto> data = page.stream().map(this::toDto).toList();
        return ResponseEntity.ok(PageDto.<ClubDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional
    public ClubDto create(CreateClubDto dto) {
        Club club = Club.builder()
                .nom(dto.getNom())
                .ville(dto.getVille())
                .region(dto.getRegion())
                .logo(dto.getLogo())
                .dateAffiliation(dto.getDateAffiliation())
                .actif(dto.getActif() != null ? dto.getActif() : true)
                .presidentNom(dto.getPresidentNom())
                .build();
        return toDto(clubRepository.save(club));
    }

    @Transactional
    public ClubDto update(Long id, UpdateClubDto dto) {
        Club club = clubRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found"));
        if (dto.getNom() != null) club.setNom(dto.getNom());
        if (dto.getVille() != null) club.setVille(dto.getVille());
        if (dto.getRegion() != null) club.setRegion(dto.getRegion());
        if (dto.getLogo() != null) club.setLogo(dto.getLogo());
        if (dto.getDateAffiliation() != null) club.setDateAffiliation(dto.getDateAffiliation());
        if (dto.getActif() != null) club.setActif(dto.getActif());
        if (dto.getPresidentNom() != null) club.setPresidentNom(dto.getPresidentNom());
        return toDto(clubRepository.save(club));
    }

    @Transactional
    public void delete(Long id) {
        Club club = clubRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found"));
        club.setDeletedAt(LocalDateTime.now());
        clubRepository.save(club);
    }

    @Transactional(readOnly = true)
    public List<AthleteDto> getAthletes(Long clubId) {
        clubRepository
                .findByIdAndDeletedAtIsNull(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found"));
        return athleteRepository.findByClub_IdAndDeletedAtIsNull(clubId).stream()
                .map(athleteService::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClubStaffDto> getStaff(Long clubId) {
        clubRepository
                .findByIdAndDeletedAtIsNull(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found"));
        return clubStaffRepository.findByClub_IdAndDeletedAtIsNull(clubId).stream()
                .map(this::toStaffDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClubStaffDto> getAllStaff() {
        return clubStaffRepository.findByDeletedAtIsNull().stream()
                .map(this::toStaffDto)
                .toList();
    }

    @Transactional
    public ClubStaffDto addStaff(Long clubId, CreateClubStaffDto dto) {
        Club club = clubRepository
                .findByIdAndDeletedAtIsNull(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("Club not found"));
        User user = userRepository
                .findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (clubStaffRepository.existsByClub_IdAndUser_IdAndDeletedAtIsNull(clubId, dto.getUserId())) {
            throw new ConflictException("Ce membre est déjà affecté à ce club.");
        }
        ClubStaff staff = ClubStaff.builder()
                .club(club)
                .user(user)
                .poste(PosteStaffEnum.valueOf(dto.getPoste()))
                .build();
        return toStaffDto(clubStaffRepository.save(staff));
    }

    @Transactional
    public void removeStaff(Long clubId, Long staffId) {
        ClubStaff staff = clubStaffRepository
                .findByIdAndDeletedAtIsNull(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff assignment not found"));
        if (!staff.getClub().getId().equals(clubId)) {
            throw new ResourceNotFoundException("Staff assignment not found for this club");
        }
        staff.setDeletedAt(LocalDateTime.now());
        clubStaffRepository.save(staff);
    }

    public ClubDto toDto(Club club) {
        return ClubDto.builder()
                .id(club.getId())
                .nom(club.getNom())
                .ville(club.getVille())
                .region(club.getRegion())
                .logo(club.getLogo())
                .dateAffiliation(club.getDateAffiliation())
                .actif(club.getActif())
                .presidentNom(club.getPresidentNom())
                .createdAt(club.getCreatedAt())
                .build();
    }

    private ClubStaffDto toStaffDto(ClubStaff cs) {
        return ClubStaffDto.builder()
                .id(cs.getId())
                .clubId(cs.getClub().getId())
                .clubNom(cs.getClub().getNom())
                .userId(cs.getUser().getId())
                .userNom(cs.getUser().getFirstName() + " " + cs.getUser().getLastName())
                .userEmail(cs.getUser().getEmail())
                .poste(cs.getPoste().name())
                .build();
    }
}
