package com.ftn.backend.service;

import com.ftn.backend.dtos.staff.CompetitionStaffDto;
import com.ftn.backend.dtos.staff.CreateCompetitionStaffDto;
import com.ftn.backend.exception.business.ConflictException;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Competition;
import com.ftn.backend.model.CompetitionStaff;
import com.ftn.backend.model.User;
import com.ftn.backend.repository.CompetitionRepository;
import com.ftn.backend.repository.CompetitionStaffRepository;
import com.ftn.backend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompetitionStaffService {

    private final CompetitionStaffRepository staffRepository;
    private final CompetitionRepository competitionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CompetitionStaffDto> getByCompetition(Long competitionId) {
        return staffRepository.findByCompetition_IdAndDeletedAtIsNull(competitionId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CompetitionStaffDto> getAll() {
        return staffRepository.findAll().stream()
                .filter(s -> s.getDeletedAt() == null)
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public CompetitionStaffDto addStaff(Long competitionId, CreateCompetitionStaffDto dto) {
        Competition competition = competitionRepository
                .findByIdAndDeletedAtIsNull(competitionId)
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found"));

        User user = userRepository
                .findByIdAndDeletedAtIsNull(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (staffRepository.existsByCompetition_IdAndUser_IdAndDeletedAtIsNull(competitionId, dto.getUserId())) {
            throw new ConflictException("User is already assigned to this competition");
        }

        CompetitionStaff staff = CompetitionStaff.builder()
                .competition(competition)
                .user(user)
                .poste(dto.getPoste())
                .build();

        return toDto(staffRepository.save(staff));
    }

    @Transactional
    public void removeStaff(Long staffId) {
        CompetitionStaff staff = staffRepository
                .findByIdAndDeletedAtIsNull(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff assignment not found"));
        staff.setDeletedAt(LocalDateTime.now());
        staffRepository.save(staff);
    }

    public CompetitionStaffDto toDto(CompetitionStaff s) {
        return CompetitionStaffDto.builder()
                .id(s.getId())
                .competitionId(s.getCompetition().getId())
                .competitionNom(s.getCompetition().getNom())
                .userId(s.getUser().getId())
                .userNom(s.getUser().getFirstName() + " " + s.getUser().getLastName())
                .userEmail(s.getUser().getEmail())
                .poste(s.getPoste())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
