package com.ftn.backend.service;

import com.ftn.backend.dtos.membership.ClubMembershipDto;
import com.ftn.backend.dtos.membership.CreateClubMembershipDto;
import com.ftn.backend.dtos.membership.UpdateClubMembershipDto;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.Club;
import com.ftn.backend.model.ClubMembership;
import com.ftn.backend.model.User;
import com.ftn.backend.repository.ClubMembershipRepository;
import com.ftn.backend.repository.ClubRepository;
import com.ftn.backend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClubMembershipService {

    private final ClubMembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;

    @Transactional(readOnly = true)
    public List<ClubMembershipDto> getByClub(Long clubId) {
        return membershipRepository.findByClub_IdAndDeletedAtIsNull(clubId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ClubMembershipDto create(CreateClubMembershipDto dto) {
        User user = userRepository
                .findByIdAndDeletedAtIsNull(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Club club = clubRepository
                .findByIdAndDeletedAtIsNull(dto.getClubId())
                .orElseThrow(() -> new ResourceNotFoundException("Club not found"));
        ClubMembership membership = ClubMembership.builder()
                .user(user)
                .club(club)
                .role(dto.getRole())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();
        return toDto(membershipRepository.save(membership));
    }

    @Transactional
    public ClubMembershipDto update(Long id, UpdateClubMembershipDto dto) {
        ClubMembership membership = membershipRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));
        if (dto.getRole() != null) membership.setRole(dto.getRole());
        if (dto.getStartDate() != null) membership.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) membership.setEndDate(dto.getEndDate());
        if (dto.getIsCurrent() != null) membership.setIsCurrent(dto.getIsCurrent());
        return toDto(membershipRepository.save(membership));
    }

    @Transactional
    public void delete(Long id) {
        ClubMembership membership = membershipRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));
        membership.setDeletedAt(LocalDateTime.now());
        membershipRepository.save(membership);
    }

    public ClubMembershipDto toDto(ClubMembership m) {
        return ClubMembershipDto.builder()
                .id(m.getId())
                .userId(m.getUser().getId())
                .userEmail(m.getUser().getEmail())
                .userFirstName(m.getUser().getFirstName())
                .userLastName(m.getUser().getLastName())
                .clubId(m.getClub().getId())
                .clubNom(m.getClub().getNom())
                .role(m.getRole())
                .startDate(m.getStartDate())
                .endDate(m.getEndDate())
                .isCurrent(m.getIsCurrent())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
